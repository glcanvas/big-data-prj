# https://tesseract-ocr.github.io/tessdoc/Compiling-%E2%80%93-GitInstallation.html

# Install Tesseract Engine
apt-get install automake ca-certificates g++ git libtool libleptonica-dev make pkg-config -y
git clone https://github.com/tesseract-ocr/tesseract.git
cd tesseract
./autogen.sh
./configure
make
sudo make install
sudo ldconfig

# Download languages
git clone https://github.com/zdenop/tessdata_downloader.git
cd tessdata_downloader
pip3 install -r requirements.txt
sudo python3 tessdata_downloader.py -o /usr/local/share/tessdata/ -r tessdata_fast -l eng
sudo python3 tessdata_downloader.py -o /usr/local/share/tessdata/ -r tessdata_fast -l rus

