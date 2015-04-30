import mongoengine
from settings.constants import getDatabase

#Database connection
def connect():
	mongoengine.connect("taskappdebug")

__author__ = ['ashwineaso','mahesmohan']