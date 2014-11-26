class TaskWithIDNotFound(Exception):
	def __init__(self):
		super(TaskWithIDNotFound, self).__init__()
		self.message = "Cannot find the task with the ID"
		self .code = 2001