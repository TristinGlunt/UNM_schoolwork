#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char * * argv) {
     FILE * fpi;
     char bm[2];
     char pad[3];
     char pix[3];
     int r, c, m;
     int messLen;
     int padBytes;
     int bit;
     int bitMask;
     int bufIndex;
     char messBuf[10000];

     struct bmp {
        int size;
        short resv1;
        short resv2;
        int imageOffset;
        int infoHeaderSize;
        int width;
        int height;
        short planes;
        short bpp;
        int compress;
        int paddedSize;
        int hres;
        int vres;
        int col1;
        int col2;
     }
     header;

     if (argc != 2) {
        printf("Usage: %s infile.bmp\n", argv[0]);
        return (1);
     }

     if ((fpi = fopen(argv[1], "rb")) == NULL) {
        printf("Unable to open infile: %s\n", argv[1]);
        return (2);
     }

     fread(bm, sizeof(char), sizeof(bm), fpi);

     if (bm[0] != 'B' || bm[1] != 'M') {
        printf("%s is not a .bmp format file\n", argv[1]);
        return (4);
     }

     fread( & header, sizeof(struct bmp), (size_t) 1, fpi);
     padBytes = (4 - (3 * header.width) % 4) % 4;
     messLen = 10000;

     bit = 7;
     bufIndex = 0;

     int t;
     int stop = 0;

     for (c = 0; c < header.height && !stop; ++c) {
        for (r = 0; r < header.width && !stop; ++r) {
           fread(pix, sizeof(char), sizeof(pix), fpi);

           if (bufIndex <= messLen) {
              for (t = 0; t < sizeof(pix); t++) {
                 // Pixel begin
                 bitMask = 1 << bit;
                 messBuf[bufIndex] =
                    messBuf[bufIndex] & (0xFF - bitMask) | (((pix[t] & 2) >> 1) << bit);

                 if (bit == 0) {
                    if (messBuf[bufIndex] == '\0') {
                       stop = 1;
                    }
                    ++bufIndex;
                 }

                 bit = (bit + 7) % 8;

                 bitMask = 1 << bit;
                 messBuf[bufIndex] =
                    messBuf[bufIndex] & (0xFF - bitMask) | ((pix[t] & 1) << bit);

                 if (bit == 0) {
                    if (messBuf[bufIndex] == '\0') {
                       stop = 1;
                    }
                    ++bufIndex;
                 }

                 bit = (bit + 7) % 8;

                 // Pixel end
              }

           }
        }

        if (padBytes != 0) {
           fread(pad, sizeof(char), (size_t) padBytes, fpi);
           if (bufIndex <= messLen && !stop) {
              for (t = 0; t < padBytes; t++) {
                 // Pixel begin
                 bitMask = 1 << bit;
                 messBuf[bufIndex] =
                    messBuf[bufIndex] & (0xFF - bitMask) | (((pad[t] & 2) >> 1) << bit);

                 if (bit == 0) {
                    if (messBuf[bufIndex] == '\0') {
                       stop = 1;
                    }
                    ++bufIndex;
                 }

                 bit = (bit + 7) % 8;

                 bitMask = 1 << bit;
                 messBuf[bufIndex] =
                    messBuf[bufIndex] & (0xFF - bitMask) | ((pad[t] & 1) << bit);

                 if (bit == 0) {
                    if (messBuf[bufIndex] == '\0') {
                       stop = 1;
                    }
                    ++bufIndex;
                 }

                 bit = (bit + 7) % 8;

                 // Pixel end
              }
           }
        }

     }

     fclose(fpi);

     messBuf[bufIndex + 1] = '\0';
     printf("%s", messBuf);

     return (0);
  }
