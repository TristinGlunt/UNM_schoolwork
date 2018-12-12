from keras import layers
from keras.layers import Input, Dense, Activation, ZeroPadding2D, BatchNormalization, Flatten, Conv2D
from keras.layers import AveragePooling2D, MaxPooling2D, Dropout, GlobalMaxPooling2D, GlobalAveragePooling2D
from keras.models import Sequential
from keras.preprocessing import image
from keras.utils import layer_utils
from keras.optimizers import SGD
from keras.models import load_model
import numpy as np
import os
from keras import backend as K
K.clear_session()

AMOUNT_OF_IMAGE_SPLITS = 30 / 2 # images / split#

# Load in pre-trained model for music classification
model = load_model("models/best_model_2splits_50epochs.h5")

X_test = np.load("X_test_split_2.dat")
# reshape so in form for CNN-Keras
X_test = X_test.reshape(X_test.shape[0], 174, 124, 1)
print(X_test.shape)

# normalize testing data
X_test = X_test / 255

# make predictions on validation data from Kaggle
predictions = model.predict_classes(X_test, verbose=1)
# Converting predictions to genre names
# print(predictions)

prediction_names = []
for prediction in predictions:
    if prediction == 0:
        prediction_names.append("blues")
    elif prediction == 1:
        prediction_names.append("classical")
    elif prediction == 2:
        prediction_names.append("country")
    elif prediction == 3:
        prediction_names.append("disco")
    elif prediction == 4:
        prediction_names.append("hiphop")
    elif prediction == 5:
        prediction_names.append("jazz")
    elif prediction == 6:
        prediction_names.append("metal")
    elif prediction == 7:
        prediction_names.append("pop")
    elif prediction == 8:
        prediction_names.append("reggae")
    else:
        prediction_names.append("rock")
# prediction_names

def convert_predictions_to_votes(prediction_names):
    print(len(prediction_names))
    # convert predictions to votes
    final_predictions = []

    keys = {"blues", "classical", "country", "disco", "hiphop", "jazz", "metal", "pop", "reggae", "rock"}
    current_song_vote = {key: 0 for key in keys}
    print(current_song_vote)

    counter = 0
    for prediction in prediction_names:
        if (counter % AMOUNT_OF_IMAGE_SPLITS == 0) and counter != 0:
            # get top vote
            top_vote = max(current_song_vote, key=current_song_vote.get)
            # add vote to list of final predictioons
            final_predictions.append(top_vote)
            # reset current votes
            current_song_vote = {key: 0 for key in keys}
            counter = 0

        counter += 1
        current_song_vote[prediction] += 1

    # get the last set of votes (still 10 votes, but since we don't go to next one, it never gets grabbed)
    top_vote = max(current_song_vote, key=current_song_vote.get)
    final_predictions.append(top_vote)
    print("Final prediction length " + str(len(final_predictions)))

    return final_predictions

final_predictions = convert_predictions_to_votes(prediction_names)
print(final_predictions)


# can just use original names
list_of_file_names = []
for file in os.listdir(os.getcwd() + "/validation_pngfiles"):
    file = file.replace("png", "au")
    file = file.split("ion")
    file = file[0] + "ion" + "." + file[1]
    print(file)
    list_of_file_names.append(file)

output_file = open("output.csv", "w")
output_file.write("id,class\n")

i = 0
for prediction in final_predictions:
    output_file.write("%s,%s\n" % (list_of_file_names[i], prediction))
    i += 1

output_file.close()
