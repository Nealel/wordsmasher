# WORDSMASHER

## what is this?
It's a highly customizable, open-source fantasy name generator. It's a tool for writers, dungeon-masters, world-builders 
and anyone wants to a lot of made-up names with a consistent, distinctive feel to them.

Wordsmasher works similarly to most other fantasy name generators (building a statistical model from a real language, 
and generating new words based on that model), but whereas other generators box you in to their pre-existing categories,
wordsmasher lets you blend different languages together to create something that's distinctive, consistent, and uniquely
yours. You want something that's 60% Celtic, 30% Germanic and 10% Bengali? No problem.


## How is it built?
Java 11 | Spring Boot | Javascript | JQuery | CSS | HTML | Bootstrap

disclaimer: I am not a frontender, and I suck at UX. All my JS, HTML and CSS is self-taught, please don't judge me too harshly.

### Name Generation
The main algorithm used to generate the words is **Markov chains**. This creates a statistical model of a language based
on input data, and uses this to generate new words that fit that model.


### Input data
The input data was gathered from behindthename.com's API as a batch job (using the `BehindTheNameCorpusBuilder` class), and sorted 
into separate files according to gender and nationality. Files were manually grouped into a hierichal structure, which 
allows the user to select a generic group (e.g. 'Scandinavian') and use all data included in that folder and its subfolders,
or select very specific groups (e.g. `Swedish (Male)`).

### API
The API is built with Spring Boot. When `/names` is called, a markov model is generated based on the users specific request
(based on their selected data source and chunk size), a batch of names is generated and returned. Since computing the 
model is slow and expensive, the results are cached to improve the performance of repeated generation.


## Building and running

This app requires java 11. It can be built with maven (`mvn spring-boot:run`) or run through an IDE. The frontend is visible
in a browser at localhost:8080


## FAQ
#### Why is the representation of non-European languages so poor?

Its limited by the corpuses that I have available to me. To generate decent words, the app needs a fairly large data set
to work from, and the data source I used did not have great data for a lot of non-European nationalities. In cases where
a data set was not large enough to be usable on its own, I decided to merge it with geographically similar
data sets rather than discard the data entirely. This regrettably results in over-generalization of non-western languages,
such as Najaho, Apache and Sioux all being grouped into 'Native American'.


## Contributing
Contributions are welcome. If you're a dev, get in touch, I'd particularly love to collaborate with a frontend developer
on this. I'm also looking for non-developers who can help me expand and improve the input data I'm using.