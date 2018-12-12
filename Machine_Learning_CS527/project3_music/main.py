import matplotlib.pyplot as plt
from scipy import signal
from scipy.io import wavfile
import numpy as np
import os
from matplotlib.pyplot import specgram
import matplotlib
from skimage.io import imread


def main():

    # NOTE: What do you want to do?
    make_images = False
    converting = True
    split_size = 3


    # Step 0
    if make_images == True:
        base_dir = "/home/anthony/git/music_classification_lstm_rnn"
        # Training
        au_dir = base_dir + "/genres"
        wav_dir = base_dir + "/color/split_" + str(split_size) + "/wavfiles"
        png_dir = base_dir + "/color/split_" + str(split_size) + "/pngfiles"
        make_fresh_data(au_dir, wav_dir, png_dir, split_size)

        # Validation
        au_dir = base_dir + "/validation"
        wav_dir = base_dir + "/color/split_" + str(split_size) + "/validation_wavfiles"
        png_dir = base_dir + "/color/split_" + str(split_size) + "/validation_pngfiles"
        make_fresh_data(au_dir, wav_dir, png_dir, split_size)

    # Step 1
    # Converting images into .dat files (numpy arrays) in preparation for training.
    if converting == True:
        convert_training_images(split_size)
        convert_validation_images(split_size)


# make_fresh_data()
# Meta method that takes .au files and makes split png files for both training
# and validation files.
def make_fresh_data(au_dir, wav_dir, png_dir, split_size):
    # Get all the base file names
    music_file_names = gen_music_file_names(au_dir)#, "blues.00000")

    # Converting all our .au files to .wav files
    convert_to_wav(music_file_names, wav_dir, split_size)

    cleanup_last_file(wav_dir, split_size)

    # Take all the wav files and make them into pictures.
    for wav_file in os.listdir(wav_dir):
       make_spectrogram(os.path.join(wav_dir, wav_file), png_dir)


# gen_music_file_names()
# Generating the full list of all music files in our code repo. Takes .au files
# and puts them into a list of absolute file paths.
def gen_music_file_names(au_dir, single_file = ""):
    music_file_names = []
    list_of_files = []
    for file_or_dir in os.listdir(au_dir):
        pre_path = os.path.join(au_dir, file_or_dir)
        if os.path.isdir(pre_path):
            for file in os.listdir(pre_path):
                list_of_files.append(os.path.join(pre_path, file))
        elif os.path.isfile(pre_path):
            list_of_files.append(pre_path)

    # Figuring out which files we should be returning
    for file in list_of_files:
        # If single_file is specified (i.e. "blues.00000"),
        # then this will only process that file.
        if file.endswith(".au") and single_file in file:
            music_file_names.append(file)

    return music_file_names


# convert_to_wav()
# Converts a given list of music filename's into wav files.
def convert_to_wav(music_file_names, wav_dir, split_size):
    for music_file_name in music_file_names:
        raw_file_name = (".".join(music_file_name.split(".")[0:2])).split("/")[-1]
        output_file_name = os.path.join(wav_dir, raw_file_name) + "-.wav"

        print("Output wav file: \n%s" % output_file_name)

        # Splitting the 30s wav into 10 3s wav's
        # Read "man sox" for more information on the command that is ran here.
        os.system("sox -e signed-integer " + music_file_name + " " + output_file_name + \
                  " trim 0 " + str(split_size) + " : newfile : restart")


# cleanup_last_file()
# When we make our clips and split them sometimes there will be a tiny
# last file that is made (usually is .1s or something). This method cleans up
# that last degenerate file.
def cleanup_last_file(wav_dir, split_size):

    # This index string will look like "-011"
    index_we_dont_want = int(30 / split_size) + 1
    if split_size < 5:
        index_string = "-0"
    else:
        index_string = "-00"

    index_string += str(index_we_dont_want)

    for file in os.listdir(wav_dir):
        full_path = os.path.join(wav_dir,file)
        if index_string in full_path:
            os.system("rm " + full_path)


# make_spectrogram()
# Makes a given music file (.wav) into a spectrogram and saves it as a png.
def make_spectrogram(file_name, png_dir):

    print("Making spectrogram for file_name: \n%s" % file_name)
    sample_rate, samples = wavfile.read(file_name)
    #figure = specgram(samples, Fs=sample_rate, xextent=(0,30)) # NOTE: Old spectrogram...
    frequencies, times, spectrogram = signal.spectrogram(samples, sample_rate)
    normalize = matplotlib.colors.Normalize(vmin = -10, vmax = 10)
    plt.pcolormesh(times, frequencies, np.log(spectrogram), norm=None, cmap='nipy_spectral')
    frame1 = plt.gca()
    fig = plt.gcf()
    # NOTE: This is inspired from original size of 483x356 images. 483 / 200 = 2.415
    fig.set_size_inches(2.415, 1.78)

    frame1.axes.get_xaxis().set_visible(False)
    frame1.axes.get_yaxis().set_visible(False)

    # Saving the figure while removing white space border
    # NOTE: If you want smaller pictures, reduce the dpi.
    base_name = (os.path.basename(file_name))[:-4]
    plt.savefig(os.path.join(png_dir, base_name + ".png"), bbox_inches='tight',
                pad_inches=-0.1, dpi=100)

    plt.close()


def convert_training_images():
    X_train = []
    y_train = []
    label = []
    current_wd = os.getcwd()
    for file in os.listdir(current_wd + "/color/split_" + str(size_of_split) + "/pngfiles"):
        print(file)
        image = imread(current_wd + "/color/split_" + str(size_of_split) + "/pngfiles/" + file)
        image = image[:, :, :3]
        # image = image.reshape([124, 174, 3])
        X_train.append(image)

        if "blues" in file:
            label = [1, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        elif "classical" in file:
            label = [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
        elif "country" in file:
            label = [0, 0, 1, 0, 0, 0, 0, 0, 0, 0]
        elif "disco" in file:
            label = [0, 0, 0, 1, 0, 0, 0, 0, 0, 0]
        elif "hiphop" in file:
            label = [0, 0, 0, 0, 1, 0, 0, 0, 0, 0]
        elif "jazz" in file:
            label = [0, 0, 0, 0, 0, 1, 0, 0, 0, 0]
        elif "metal" in file:
            label = [0, 0, 0, 0, 0, 0, 1, 0, 0, 0]
        elif "pop" in file:
            label = [0, 0, 0, 0, 0, 0, 0, 1, 0, 0]
        elif "reggae" in file:
            label = [0, 0, 0, 0, 0, 0, 0, 0, 1, 0]
        elif "rock" in file:
            label = [0, 0, 0, 0, 0, 0, 0, 0, 0, 1]

        y_train.append(label)

    print(np.array(X_train).shape)
    print(np.array(y_train).shape)
    np.array(X_train).dump("X_train_" + str(size_of_split) + ".dat")
    np.array(y_train).dump("y_train_" + str(size_of_split) + ".dat")

def convert_validation_images():
    X_test = []
    current_wd = os.getcwd()
    for file in os.listdir(current_wd + "/color/split_" + str(size_of_split) + "/validation_pngfiles"):
        print(file)
        image = imread(current_wd + "/color/split_" + str(size_of_split) + "/validation_pngfiles/" + file)
        image = image[:, :, :3]
        # image = image.reshape([124, 174, 3])
        print(image.shape)
        X_test.append(image)

    print(np.array(X_test).shape)
    np.array(X_test).dump("X_test_" + str(size_of_split) + ".dat")




if __name__ == "__main__":
    main()
