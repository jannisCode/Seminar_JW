import math

import requests
import zmq
import numpy as np
import cv2
import time
import json
import base64

from deepface import DeepFace
from deepface.modules.streaming import analysis
from keras.src.backend.tensorflow.trainer import convert_to_np_if_not_ragged

url = "https://llm.k8s.iism.kit.edu/analyze"


class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.float32):
            return float(obj)
        if isinstance(obj, np.float64):
            return float(obj)
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return super(NumpyEncoder, self).default(obj)

def sendBack(data):
    outsocket.send(data.encode('utf-8'))

def getDominantEmotion(data) -> str:
    try:
        dominant_emotion = data['results'][0]['dominant_emotion']
        return dominant_emotion
    except Exception:
        return ""

def euclidean(x1: int, y1 : int, x2 : int, y2 : int) -> float:
    return math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))

# which face belongs to who
def closestDetectedFace(x: int, y: int):
    print("closestDetectedFaceSearched")

# Load configuration
with open('launch.json') as f:
  config = json.load(f)
print(config)

# Setup the sockets
context = zmq.Context()
jsonString = ""

# Input camera feed from furhat using a SUB socket
insocket = context.socket(zmq.SUB)
insocket.setsockopt_string(zmq.SUBSCRIBE, '')
insocket.connect('tcp://' + config["Furhat_IP"] + ':3000')
insocket.setsockopt(zmq.RCVHWM, 1)
insocket.setsockopt(zmq.CONFLATE, 1)  # Only read the last message to avoid lagging behind the stream.

# Output results using a PUB socket
context2 = zmq.Context()
outsocket = context2.socket(zmq.PUB)
outsocket.bind("tcp://" + config["Dev_IP"] + ":" + config["detection_exposure_port"])

print('connected, entering loop')
prevset = {}
iterations = 0
detection_period = config["detection_period"] # Detecting objects is resource intensive, so we try to avoid detecting objects in every frame
detection_threshold = config["detection_confidence_threshold"] # Detection threshold takes a double between 0.0 and 1.0

x = True
while x:

    string = insocket.recv()
    magicnumber = string[0:3]
    # check if we have a JPEG image (starts with ffd8ff)
    if magicnumber == b'\xff\xd8\xff':
        starttime = time.time()
        print(iterations)
        if (iterations % detection_period == 0):

            image_base64 = base64.b64encode(string).decode('utf-8')
            image_base64 = "data:image/jpeg;base64," + image_base64
            print(" hehe")
            data = {
                "img_path": image_base64,  # Use the Base64-encoded string
                "actions": ["emotion"]
            }

            analysis = requests.post(url, json=data)

            print(analysis)
            sendBack(analysis.json())
            print("Analysis from Deepface: " , getDominantEmotion(analysis.json()))
            print("taken time for detect", time.time()-starttime)
        iterations = iterations + 1
    else:
        print("else")
        print(" ")
        jsonString = string

    k = cv2.waitKey(1)
    if k%256 == 27: # When pressing esc the program stops.
        # ESC pressed
        print("Escape hit, closing...")
        break


