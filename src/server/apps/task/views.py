__author__ = ["ashwineaso"]
from bottle import request
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
from . import bll

def addNewTask():
	response = {}
	data = {}
	taskObj = Collection()
	userObj = Collection()
	obj = request.json
	try:
		taskObj.owner = obj["owner"]
		taskObj.priority = obj["priority"]
		taskObj.name = obj["name"]
		taskObj.description = obj["description"]
		taskObj.dueDateTime = obj["dueDateTime"]
		taskObj.status = obj["status"]
		taskObj.collaborators = obj["collaborators"]
		task = bll.addNewTask(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response