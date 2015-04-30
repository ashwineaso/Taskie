__author__ = ["Ashwin Easo"]

import smtplib
import boto.ses
from models import *
from . import dal
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from settings.altEngine import Collection

taskie_mail_invite = "Taskie Team <noreply@taskie.me>"
taskie_mail_verification = "Taskie Email Verification <noreply@taskie.me>"
taskie_mail_password_reset = "Taskie Password Reset <noreply@taskie.me>"
HOST = "email-smtp.us-west-2.amazonaws.com"
PORT = "25"
SES_KEY = "AKIAIPC67FFTNLDSAXHA"
SES_SECRET = "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"


def sendInvite(userObj):
	inviter = dal.getUserById(userObj.user)
	invited_by = inviter.email
	invite_to = userObj.email
	token = Token.objects.get(user = inviter)

	# Create the body of the message (a plain-text and an HTML version).
	html = """\
	<html>
		<head></head>
		<body>
			<p>Hi!<br>
			<br>Greetings from Taskie,<br>
			<br> %s has sent you a task via our task sharing and collaboration app Taskie. If you wish to collaborate 
			via Taskie, please dowload the app from Google Play Store<br>
			or simply click the link below.<br>
			<br>www.play.google.com/com.altersense.taskie<br>
			<br>For more information and support, please feel free to send us an email at someone@taskie.me.
			Our support team will be getting back to you within 24 Hrs.<br>
			<br>Thanks and Warm Regards,<br>
			<br>Team Taskie
			</p>
		</body>
	</html>
	""" % (inviter.name)
	
	Subject = "You have been invited to try out Taskie"

	#Send the message via local smtp server
	conn = boto.ses.connect_to_region(
        'us-west-2',
        aws_access_key_id=SES_KEY,
        aws_secret_access_key=SES_SECRET)
	conn.send_email(taskie_mail_invite, Subject, "", invite_to, html_body=html)



def sendVerification(user):
	"""
	Send verification mail to the user and proide confirmation link for verification

	::type userObj: object
	::param userObj: instance of User class with the following attributes
					email
					name
					**kwargs
	"""
	userObj = Collection()
	userObj.user = user
	token = dal.getTokenByUser(userObj)
	userObj.key = token.refresh_token

	link = """http://taskie.me/user/verifyEmail/%s/%s""" % (userObj.user.email,userObj.key)
	delete = """http://taskie.me/user/deleteAccount/%s/%s""" % (userObj.user.email,userObj.key)
	html = """\
	<html>
		<head></head>
		<body>
			<p>Hi! %s<br>
			<br>Greetings from Taskie,<br>
			<br>Thanks for creating a Taskie account and being a part of our services. You are now just one step away 
			from the Taskie experience.<br>
			<br>As part of our security policy, we are required to verify the email id of every user registering to our 
			service, before their account is activated. <strong>Please visit the link below to activate your account and start 
			using Taskie.</strong> 
			<br> %s <br>
			<br>If the account wasn't created by you, please delete the account by clicking the following link : %s<br>
			Failing to do so may prevent you from creating a Taskie account using the email id. 
			<br>For more information and support, please feel free to send us an email at someone@taskie.me.
			Our support team will be getting back to you within 24 Hrs.<br>
			<br>Thanks and Warm Regards,<br>
			<br>Team Taskie
			</p>
		</body>
	</html>
	""" % (user.name, link, delete)

	Subject = " Taskie Account Verification"

	#Send the message via local smtp server
	conn = boto.ses.connect_to_region(
        'us-west-2',
        aws_access_key_id=SES_KEY,
        aws_secret_access_key=SES_SECRET)
	conn.send_email(taskie_mail_verification, Subject, "", userObj.user.email, html_body=html)


def passwordReset(userObj):
	"""Send a password reset mail to the user along with email and key"""
	token = dal.getTokenByUser(userObj)

	link = """http://taskie.me/user/updatePassword/%s/%s""" % (userObj.user.email, token.key)
	html = """\
	<html>
		<head></head>
		<body>
			<p>Hi! %s<br>
			<br>Greetings from Taskie,<br>
			<br>A password reset request was recently made using your email id : %s .<br>
			If the request was made by you, please visit the following link and provide a new password : %s.
			<br>If the request wasn't made by you, please ignore this mail.<br>
			<br>For more information and support, please feel free to send us an email at someone@taskie.me.
			Our support team will be getting back to you within 24 Hrs.<br>
			<br>Thanks and Warm Regards<br>
			<br>The Taskie Team.
			</p>
		</body>
	</html>
	"""  % (userObj.user.name, userObj.user.email, link)

	Subject = " Taskie Account - Password Reset"

	#Send the message via local smtp server
	conn = boto.ses.connect_to_region(
        'us-west-2',
        aws_access_key_id=SES_KEY,
        aws_secret_access_key=SES_SECRET)
	conn.send_email(taskie_mail_password_reset, Subject, "", userObj.user.email, html_body=html)
