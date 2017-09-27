# companion-classification-micro


This is the repository for a Weka based microservice to classify text.

The microservice contains trained classifier used to predict several 
classes of a given set of english sentences as input. A sentence must 
be preprocessed by an nlp implementation, since most classifier are using
the PENN TAG set as input. 

This projects contains this set of classifiers:

* _TextClassifier_ Naive Bayes based classifier to determine the topic of a sentence
* _SentenceClassifier_ J48 based classifier to predict the type of a sentence (ex. declarative, imperative, WH-Question type, etc.)

Each classifier produces a map with the probability values for each class.

## Building

To build the service call:

```
> mvn clean install
```   

To build the docker image:

```
> docker build -t companion-classification-micro .
```

