# Corpus

Names are generated based on a statistical model which is created from a set of input names. In order to generate quality
names, the input corpus must be A) large, B) high quality and C) organized into categories

The data corpus was generated as a one-time job, using a combination of automated and manual processes.
For reference (and in case the process needs to be repeated), this is how it was done:

1. **Download Raw Data:** a huge list of unsorted names was downloaded from https://www.behindthename.com/api/download.php .
This is saved in `resources/data/btn_raw/btn_givennames.txt`

2. **Collect metadata:** `BehindTheNameCorpusBuilder` loads up the raw list, and for each name, it calls behindTheName API to collect metadata about the name's gender and culture.
It then writes each name to `btn_rich`, where the file name contained the culture and gender, e.g. a french female name would be appended to the `btn_rich/french_f.txt` file.

3. **Cleaning the files:** `FileFixer` renamed to a user-facing naming format

4. **Organizing files:** files were manually organized into directories based on language groups and then subgroups, and 
these files were placed in `data/current_corpus`
e.g. `french` was organized into `European/Romance/french`. 
This nested structure allows the user to select specific files or broad groups, depending on their needs.