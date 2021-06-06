# Image-To-Text

We are using Tesseract -- Open-Source OCR model, detecting russian and english languages (in our case).
## Usage:
1. Install using `tesseract_installation.sh`;
2. Having image `path/to/image_01.jpg`, launch `tesseract path/to/image_01.jpg path/to/image_01 -l rus+eng`;
3. Get extracted text from `path/to/image_01.txt` file.