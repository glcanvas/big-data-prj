import pickle
import subprocess
from os import listdir, remove
from os.path import isfile, join, splitext

import pandas as pd


def execute_command(command) -> None:
    exitcode = subprocess.call(command, shell=True)
    if exitcode == 1:
        print('Something happened during OCR.')
    return exitcode


class Analyzer:
    def __init__(self, vectorizer_path, regressor_path):
        assert isfile(vectorizer_path), f'Can\'t find file at {vectorizer_path}'
        assert isfile(regressor_path), f'Can\'t find file at {regressor_path}'

        with open(vectorizer_path, 'rb') as f:
            self.vectorizer = pickle.load(f)

        with open(regressor_path, 'rb') as f:
            self.regressor = pickle.load(f)

    def predict_text_toxicity(self, text):
        text_transformed = self.vectorizer.transform(pd.Series([text]))
        prediction = self.regressor.predict_proba(text_transformed)[:, 1][0]
        return prediction

    def predict_image_toxicity(self, image_path):
        assert isfile(image_path), f'Can\'t find image at {image_path}'
        text_path = splitext(image_path)[0]
        execute_command(f'tesseract {image_path} {text_path} -l eng')
        try:
            text_path = f'{text_path}.txt'
            with open(text_path, 'r') as f:
                text = f.read()
            remove(text_path)
            return text, self.predict_text_toxicity(text)
        except:
            return '', 0


if __name__ == "__main__":
    vectorizer_path = 'ml_text_processing/weights/vectorizer.pkl'
    regressor_path = 'ml_text_processing/weights/regressor.pkl'
    texts = ['fuck you', 'Hi, friend!', 'Nice', 'Black Lives Matter',
             'Awful', 'Romantic', 'Awfully romantic',
             'leather man', 'Be respectful to everybody', 'Asshole', 'ecology',
             'Being transgender is amazing',
             'Being transgender is gay']

    analyzer = Analyzer(vectorizer_path, regressor_path)

    for text in texts:
        prediction = analyzer.predict_text_toxicity(text)
        print(f'Text ```{text}``` is {round(prediction*100)}% toxic.')

    images_path = join('ml_text_processing', 'data', 'images')
    images_paths = [el for el in listdir(images_path) if splitext(el)[1] != '.txt']
    images_paths.sort()
    for image_path in images_paths:
        prediction = analyzer.predict_image_toxicity(join(images_path, image_path))
        print(f'Text extracted from {image_path} is ```\n{prediction[0].strip()}\n```\nIt\'s toxicity is {round(prediction[1]*100)}%')
