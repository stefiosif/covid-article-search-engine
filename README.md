# Covid19-related search engine

A text search engine using articles related to the
non-medical impacts that Covid19 brought. The back-end
is built with Apache Lucene library whereas the front-end
is build with JavaFX and Scene Builder.

## Apache Lucene
Lucene uses an inverted index which means that instead
of mapping articles to keywords, keywords are mapped
to articles.
This makes the search engine search through the created
index instead of searching inside the article text.
Each Lucene index contains a number of Lucene documents
which is a set of Lucene fields. In this index, each
document is a covid article and each article contains
the following fields related to said article:
- release date
- field of focus (e.g. finance, business)
- title
- content

After the creation of the index, queries can be performed
and specific fields can be retrieved by the user.

## JavaFX
The JavaFX library and Scene Builder provide the GUI of
the application. After the initial "plain" query there
are options to create a more advanced search using
Lucene's field named "field of focus" or sort the
results based on relevancy and release date. The results
are presented in batches. Each result contains the
article's name and a short part of the article around the
keyword that was given by the user with bold texture.

## Example images

### Landing Page

![Screenshot_0](https://user-images.githubusercontent.com/17260204/179395322-e5a60770-ec90-4885-8dc7-27f5763c56df.png)

### Results Page

![Screenshot_1](https://user-images.githubusercontent.com/17260204/179395324-17122466-775d-49d7-a8ae-addc0f397acb.png)

### Article Reader

![Screenshot_2](https://user-images.githubusercontent.com/17260204/179395326-3528e66a-b155-4edd-9b9a-44de9df5857c.png)

