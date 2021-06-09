# Toxicity analyzer pipeline
1. Recognize text on the given image with `tesseract` OCR;
2. Predict toxicity of the text using ML model. 

## OCR
We are using Tesseract -- Open-Source OCR model, detecting russian and english languages (in our case).
### Usage:
1. Install using `tesseract_installation.sh`;
2. Having image `path/to/image_01.jpg`, launch `tesseract path/to/image_01.jpg path/to/image_01 -l eng`;
3. Get extracted text from `path/to/image_01.txt` file.

## Toxicity analyzer
We trained classifier to identify toxicity, based on the dataset given in [this competition](https://www.kaggle.com/c/jigsaw-toxic-comment-classification-challenge), there is a large number of Wikipedia comments which have been labeled by human raters for toxic behavior. The pipeline was the following: apply TF-IDF Vectorizer and Logistic Regression model after it. The obtaine ROC AUC score on 5-fold CV is `0.9587`, you can check the implementation details in `train.py` file.

<b>We are SORRY IN ADVANCE for inappropriate words and phrases</b>, we just want to show how the model works while making predictions:
```
Text ```fuck you``` is 100% toxic.
Text ```Hi, friend!``` is 6% toxic.
Text ```Nice``` is 11% toxic.
Text ```Black Lives Matter``` is 14% toxic.
Text ```Awful``` is 14% toxic.
Text ```Romantic``` is 10% toxic.
Text ```Awfully romantic``` is 10% toxic.
Text ```leather man``` is 25% toxic.
Text ```Be respectful to everybody``` is 11% toxic.
Text ```Asshole``` is 89% toxic.
Text ```ecology``` is 10% toxic.
Text ```Being transgender is amazing``` is 9% toxic.
Text ```Being transgender is gay``` is 85% toxic.
```
As we obtained those results, we can imply that the model should perform good for VK posts:

### Usage:
1. Install `tesseract` from previous "Image-To-Text" section;
2. Install Python dependencies using `pip3 install -r requirements.txt`;
3. Use `Analyzer` class from `Analyzer.py` file with pretrained model weights.  
Example can be found in the file itself.