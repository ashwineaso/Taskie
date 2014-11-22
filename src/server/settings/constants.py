#Database name
def getDatabase():
	if DEBUG :
		return "taskappdebug" 
	else:
		return "taskapp"


#Current version of the API
CURRENT_VERSION = 0.1

#Debig status of the API
DEBUG = True

__author__ = ['mahesmohan','ashwineaso']