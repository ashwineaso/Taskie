__author__ = ["ashwineaso"]

def addtask():
	response = {}
	data = {}
	taskObj = Collection()
	collabObj = Collection()
	obj = request.json
	try:
		taskObj.owner = obj["owner"]
		taskObj.priority = onj["priority"]
		taskObj.name = obj["name"]
		taskObj.description = obj["description"]
		taskObj.dueDateTime = obj["dueDateTime"]
		taskObj.status = obj["status"]
		collabObj = obj["collaborators"]
		taskObj.collaborators = collabObj