## Instructions

You will build a sample ETL application that will consume data from some webpages/API, perform a summary of the consumption and upload those results to another location.

## The task

### Background

Say we were interested in following the success of American Idol finalists after they leave the show.

One metric of success could be considered signing with a record label.

Wikipedia provides a list of these artists:

https://en.wikipedia.org/wiki/List_of_American_Idol_finalists

The linked articles for each artist may include a list of record labels in their "summary box".

### Problem

The given task is to ingest the content from each artist's page in the list of finalists, and output a collection of JSON files.

The output should include one "summary" file, which counts the number of artists signed to a label (at any time), and one file for each "label" encountered.

#### Sample summary file:

```json
{
  "Atlantic": 5,
  "RCA": 2,
  "Astral": 12,
  "NO_LABEL": 27
}
```

The `NO_LABEL` key should include the number artists who are not associated with any label.

#### Sample label file:

```json
{
  "label": "Hollywood",
  "artists": ["Just Sam", "Alejandro Aranda", "Laine Hardy", "Maddie Poppe"]
}
```

Hint: you can retrieve page contents as JSON without an API token: https://en.wikipedia.org/w/api.php?action=parse&page=List_of_American_Idol_finalists&format=json

### Uploading files

In order to upload the file(s) you have generated, you will first obtain a signed URL from this endpoint:

`https://us-central1-hifi-sandbox.cloudfunctions.net/getSignedUrl?apiKey=<API_KEY>`

You will be provided an `API_KEY` to use. This must be a `POST` request and the body must contain two fields, the name of a file you wish upload and the content-type of the file.

`// check with google documentation for valid content-types, like application/json or a binary file type like application/octet-stream`

```json
{ "fileName": "fileName", "contentType": "text/csv" }
```

The name of each file you upload should begin with a valid `uuid` which you will also be provided, please place a **forward slash** between the uuid and the rest of the file name.

`d1252a57-c3c6-4a12-a15a-6e991fb4167f/rest_of_filename.extension`

Here is a sample request that would produce a signed URL:

```bash
curl -X POST -d '{"fileName":"uuid/fileName","contentType":"text/csv"}' -H "Content-Type: application/json" "https://us-central1-hifi-sandbox.cloudfunctions.net/getSignedUrl?apiKey=<shhh...>"
```

The signed URL you retrieve will look something like this:

`https://storage.googleapis.com/audition-test/fileName?GoogleAccessId=hifi-sandbox%40appspot.gserviceaccount.com&Expires=1592541602&Signature=dJ3IyyiPviQPvYTlLNGsQ0Ggod31D3fl7q%2BuqTlqU1BEt4dAz7NbwFyOyJCWCTfhaZT8Iu3FoMuVW%2FvZ6h3BauUr00hmFS3COz6ACr0H9VIndfkclAxsVDjra%2Fn0pNXaPpKI4M1J8jfrDwwW%2BAqYQB2vzTe6M4cLDZ7WTCMndErIFzGs%2Fb2rUdQcrVtojTpFEvWZqKUS1K%2BXiDvC0%2FtOETxzhQiWJNE7XwdxhdrGNuPb4UYXmZ%2BDKoUf2zsE%2BUKQ2i3pXEUtczhU%2Fooy1zSfcbdtd3y6Ojiy7crfZR6JZG0UgxLAKe7i9ojznr6tbLUBZUfPgOb4J1L6efKpDMDmBw%3D%3D`

After retrieveing the signed URL, you must then make a second request to upload the file itself.

_Two important notes_: this endpoint will have an expiration of **FIVE MINUTES**, however you may ask for multiple URLs when testing or when uploading. Second, the `content-type` header of the upload request here _MUST MATCH_ the `contentType` you provided when requesting the signed URL. Here is what a request might look like utilizing this URL:

```bash
curl --location --request PUT 'https://storage.googleapis.com/audition-dev/1ae8c7b9-7fd4-4aca-a7b4-81c8a23f003a__file_one.csv?GoogleAccessId=hifi-sandbox%40appspot.gserviceaccount.com&Expires=1592575413&Signature=aWF6dWdnSMh84mwRWB0vxgFKE4AqTmGZSW0rLJDvPrWV4vWjmP%2BsLesuU3R2HOsDAmxR0b0hNHODBpogUGr18fG5CZp5IGb6GHpLFGkopvcDpqh0qiOMT9jB%2BA3q%2B6G63Z5T6cC%2F3x%2Fegi6PnLr6yO5d7wkLmaSFjrTyqKao0kuKelobAw5ZyDqwACG1CR4hsERGYQd%2Boxw8Iesya77G7NXTki5B3NgZjlUx9UEomrcHlrS0xwFgm79gRmb8c60V7IMR3ZUNxuQNUprSUcCkeEglaBqbRebGnIwE6irVe51VR%2FzesYdYu4BKuhNnMt8Tq1AKCMJesDvvzAcfRMyrZQ%3D%3D' \
--header 'Content-Type: text/csv' \
--data-binary '@/path/to/file.csv'
```

### Considerations

1. Feel free to use any libraries or frameworks you'd like, ultimately we are not looking for a bullet proof production grade system, but a reasonable display of code quality and composability in accomplishing this task, do not overengineer.
2. While we do not require you to build it in Scala or in a functional style, both would be strongly preferred.
3. It should gracefully handle errors including from our endpoint.
4. Unit tests.
5. It does not have to be invocable via endpoint, though if you think that's easier go ahead, just provide instructions for running it/testing it, a console app is just fine.
6. While not a hard requirement, much of our code operates asynchronously, we would expect to see this in a strong submission.

### Bonuses

1. Downloading/Transforming/Uploading happens in parallel or as a set of stream operations
2. Retry logic

### Sending Submission

When you are ready to submit, please zip up your code and email it to: `patrick@hi.fi`
