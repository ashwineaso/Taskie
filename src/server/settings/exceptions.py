class TaskWithIDNotFound(Exception):
	"""docstring for TaskWithIDNotFound"""
	def __init__(self):
		super(TaskWithIDNotFound, self).__init__()
		self.msg = "Task With ID Not Found"
		self.code = 2001

	def __str__(self):
		return self.msg


class GroupWithIDNotFound(Exception):
	"""docstring for GroupWithIDNotFound"""
	def __init__(self):
		super(GroupWithIDNotFound, self).__init__()
		self.msg = "Group With ID Not Found"
		self.code = 4001

	def __str__(self):
		return self.msg
		


class UserNotFound(Exception):
	"""docstring for UserNotFound"""
	def __init__(self):
		super(UserNotFound, self).__init__()
		self.msg = "User Not Found"
		self.code = 1001 

	def __str__(self):
		return self.msg
		

class PasswordMismatch(Exception):
	"""docstring for PasswordMismatch"""
	def __init__(self):
		super(PasswordMismatch, self).__init__()
		self.msg = "Password Mismatch"
		self.code = 1002

	def __str__(self):
		return self.msg
		

class UserAlreadyExists(Exception):
	"""docstring for UserAlreadyExists"""
	def __init__(self):
		super(UserAlreadyExists,self).__init__()
		self.msg = "User already exists"
		self.code = 1003 

	def __str__(self):
		return self.msg


class UserPendingConfirmation(Exception):
	"""docstring for UserPendingConfirmation"""
	def __init__(self):
		super(UserPendingConfirmation, self).__init__()
		self.msg = "User Registered, Pending Confirmation"
		self.code = 1004
		
	def __str__(self):
		return self.msg
		

class TokenNotFound(Exception):
	"""docstring for TokenNotFound"""
	def __init__(self):
		super(TokenNotFound, self).__init__()
		self.msg = "Token Not Found"
		self.code = 3001

	def __str__(self):
		return self.msg


class AuthenticationError(Exception):
	"""docstring for AuthenticationError"""
	def __init__(self):
		super(AuthenticationError, self).__init__()
		self.msg = "Authentication Failed"
		self.code = 1005

	def __str__(self):
		return self.msg


class AccessTokenInvalid(Exception):
	"""docstring for AccessTokenInvalid"""
	def __init__(self):
		super(AccessTokenInvalid, self).__init__()
		self.msg = "Access Token Invalid"
		self.code = 3002

	def __str__(self):
		return self.msg


class RefreshTokenInvalid(Exception):
	"""docstring for RefreshTokenInvalid"""
	def __init__(self):
		super(RefreshTokenInvalid, self).__init__()
		self.msg = "Refresh Token Invalid"
		self.code = 3003

	def __str__(self):
		return self.msg


class AccessTokenExpired(Exception):
	"""docstring for AccessTokenExpired"""
	def __init__(self):
		super(AccessTokenExpired, self).__init__()
		self.msg = "Access Token Expired"
		self.code = 3004

	def __str__(self):
		return self.msg


class UserNotMember(Exception):
	"""docstring for UserNotMember"""
	def __init__(self):
		super(UserNotMember, self).__init__()
		self.msg = "User Not Member"
		self.code = 4002

	def __str__(self):
		return self.msg

class AppVersionDepreciated(Exception):
	"""docstring for UserNotMember"""
	def __init__(self):
		super(AppVersionDepreciated, self).__init__()
		self.msg = "The application cuurently installed is of an older version. Please update Immediately"
		self.code = 0010

	def __str__(self):
		return self.msg

