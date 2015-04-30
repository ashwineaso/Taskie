import mongoengine
from settings.constants import getDatabase

#Database connection
def connect():
	mongoengine.connect("taskapp")

__author__ = ['ashwineaso','mahesmohan']