import io
import pickle
import subprocess
from os import listdir, remove
from os.path import isfile, join, splitext

import pandas as pd
import pytesseract
from PIL import Image


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

    def predict_image_toxicity(self, image_path, image_data):
        assert image_path is not None or image_data is not None, 'At least one argument should not be `None`'
        try:
            if image_path is not None:
                image = Image.open(image_path) 
            else:
                image = Image.open(io.BytesIO(image_data))
            recognized_text = pytesseract.image_to_string(image, lang='eng')
            return recognized_text, self.predict_text_toxicity(recognized_text)
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

    # Example with path to image
    for image_path in images_paths:
        prediction = analyzer.predict_image_toxicity(join(images_path, image_path), None)
        print(f'Text extracted from {image_path} is ```\n{prediction[0].strip()}\n```\nIt\'s toxicity is {round(prediction[1]*100)}%')

    # Example with raw image bytes
    for image_path in images_paths:
        with open(join(images_path, image_path), 'rb') as f:
            image_data = f.read()
        prediction = analyzer.predict_image_toxicity(None, image_data)
        print(f'Text extracted from {image_path} is ```\n{prediction[0].strip()}\n```\nIt\'s toxicity is {round(prediction[1]*100)}%')
