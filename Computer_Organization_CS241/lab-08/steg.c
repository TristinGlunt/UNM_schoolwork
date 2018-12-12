#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char * * argv) {
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

   FILE * fpi, * fpo;
   char bm[2];
   char pad[3];
   char pix[3];
   int r, c, m;
   int messLen;
   int padBytes;
   int bit;
   int bufIndex;
   int bufLen;
   char messBuf[10000];
   char lineBuf[1000];


   if (argc != 3) {
      printf("Usage: %s infile.bmp outfile.bmp\n", argv[0]);
      return (1);
   }

   if ((fpi = fopen(argv[1], "rb")) == NULL) {
      printf("Unable to open infile: %s\n", argv[1]);
      return (2);
   }

   if ((fpo = fopen(argv[2], "wb")) == NULL) {
      printf("Unable to open outfile: %s\n", argv[2]);
      return (3);
   }

   fread(bm, sizeof(char), sizeof(bm), fpi);

   if (bm[0] != 'B' || bm[1] != 'M') {
      printf("%s is not a .bmp format file\n", argv[1]);
      return (4);
   }

   fread( & header, sizeof(struct bmp), (size_t) 1, fpi);

   if (header.bpp != 24) {
      printf("%s only works on 24 bpp files\n", argv[0]);
      return (5);
   }

   fwrite(bm, sizeof(char), sizeof(bm), fpo);
   fwrite( & header, sizeof(struct bmp), (size_t) 1, fpo);

   padBytes = (4 - (3 * header.width) % 4) % 4;

   messLen = 1000;

   fgets(messBuf, messLen, stdin);

   while(fgets(lineBuf, messLen, stdin)){
     strcat(messBuf, lineBuf);
   }

   messLen = strlen(messBuf);
   
   bufLen = messLen + 1;
   bit = 7;
   bufIndex = 0;

   messBuf[messLen] = 0;
   messBuf[messLen + 1] = '\0';

   char lsbs;
   int t;
   for (c = 0; c < header.height; ++c) {
      for (r = 0; r < header.width; ++r) {
         fread(pix, sizeof(char), sizeof(pix), fpi);

         if (bufIndex < bufLen) {

            for (t = 0; t < sizeof(pix); t++) {
               // Pixel array
               lsbs = 0;
               pix[t] = (pix[t] & 0xFC);

               lsbs = ((messBuf[bufIndex] & (1 << bit)) > 0);
               if (bit == 0)
                  ++bufIndex;
               bit = (bit + 7) % 8;

               lsbs = lsbs << 1;
               lsbs = lsbs | ((messBuf[bufIndex] & (1 << bit)) > 0);
               if (bit == 0)
                  ++bufIndex;
               bit = (bit + 7) % 8;

               pix[t] = pix[t] | lsbs;
               // Pixel array end
            }

         }

         fwrite(pix, sizeof(char), sizeof(pix), fpo);

      }

      if (padBytes != 0) {

         fread(pad, sizeof(char), (size_t) padBytes, fpi);
         if (bufIndex < bufLen) {
            for (t = 0; t < padBytes; t++) {
               // Pixel array
               lsbs = 0;
               pad[t] = (pad[t] & 0xFC);

               lsbs = ((messBuf[bufIndex] & (1 << bit)) > 0);
               if (bit == 0)
                  ++bufIndex;
               bit = (bit + 7) % 8;
               lsbs = lsbs << 1;
               lsbs = lsbs | ((messBuf[bufIndex] & (1 << bit)) > 0);

               if (bit == 0)
                  ++bufIndex;
               bit = (bit + 7) % 8;

               pad[t] = pad[t] | lsbs;
               // Pixel array end
            }
         }
         fwrite(pad, sizeof(char), (size_t) padBytes, fpo);
      }

   }

   fclose(fpi);
   fclose(fpo);
   return 0;
}
