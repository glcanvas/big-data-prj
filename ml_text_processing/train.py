# You can download train and test files at https://www.kaggle.com/c/jigsaw-toxic-comment-classification-challenge/data
import os
import pickle
from os.path import dirname

import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import cross_val_score


def train_model(vectorizer_path, regressor_path):
    class_name = 'toxic'

    train = pd.read_csv('ml_text_processing/data/train.csv',
                        usecols=['comment_text', class_name]).fillna(' ')
    test = pd.read_csv('ml_text_processing/data/test.csv',
                       usecols=['comment_text']).fillna(' ')

    train_text = train['comment_text']
    test_text = test['comment_text']
    all_text = pd.concat([train_text, test_text])

    vectorizer = TfidfVectorizer(
        sublinear_tf=True,
        strip_accents='unicode',
        analyzer='word',
        token_pattern=r'\w{1,}',
        stop_words='english',
        ngram_range=(1, 1),
        max_features=10000)

    print('=> Fitting word vectorizer')
    vectorizer.fit(all_text)
    train_features = vectorizer.transform(train_text)
    test_features = vectorizer.transform(test_text)

    train_target = train[class_name]
    regressor = LogisticRegression(C=0.1, solver='sag')

    print('=> Computing CV')
    cv_score = np.mean(cross_val_score(
        regressor, train_features, train_target, cv=5, scoring='roc_auc'))
    print(f'CV score is {cv_score:.4f}')

    print('=> Fitting Logistic Regression')
    regressor.fit(train_features, train_target)

    print('=> Predicting Test Set')
    prediction = regressor.predict_proba(test_features)[:, 1]

    print(f'=> Saving word vectorizer to {vectorizer_path}')
    os.makedirs(dirname(vectorizer_path), exist_ok=True)
    with open(vectorizer_path, 'wb') as f:
        pickle.dump(vectorizer, f)

    print(f'=> Saving regressor to {regressor_path}')
    os.makedirs(dirname(regressor_path), exist_ok=True)
    with open(regressor_path, 'wb') as f:
        pickle.dump(regressor, f)


def test_model(texts, vectorizer_path, regressor_path):
    with open(vectorizer_path, 'rb') as f:
        vectorizer = pickle.load(f)
    with open(regressor_path, 'rb') as f:
        regressor = pickle.load(f)

    texts_transformed = vectorizer.transform(pd.Series(texts))
    prediction = regressor.predict_proba(texts_transformed)[:, 1]

    for i, text in enumerate(texts):
        print(f'Text ```{text}``` is {round(prediction[i]*100)}% toxic.')


if __name__ == "__main__":
    vectorizer_path = 'ml_text_processing/weights/vectorizer.pkl'
    regressor_path = 'ml_text_processing/weights/regressor.pkl'
    texts = ['fuck you', 'Hi, friend!', 'Nice', 'Black Lives Matter',
             'Awful', 'Romantic', 'Awfully romantic', 
             'leather man', 'Be respectful to everybody', 'Asshole', 'ecology', 
             'Being transgender is amazing', 
             'Being transgender is gay']

    train_model(vectorizer_path, regressor_path)
    test_model(texts, vectorizer_path, regressor_path)
