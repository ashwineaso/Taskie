__author__ = ["ashwineaso"]

from settings.constants import GCMPost, TOKEN_GCM_REGISTRATION_IDS, UrlPostThread
from apps.users import dal as userdal
from apps.task import dal as taskdal
from apps.group import dal as groupdal
from settings.altEngine import Collection, SyncClass

def pushSyncNotification(syncObj):
    """
    Initiates a server side push to all the collaborators
    of the task
    """

    #Create a GCMPost object for Android Push
    androidPush = GCMPost()
    #List to store GCM ids
    androidPayload = []
    userObj = Collection()

    #Create a pseudo switch case
    #Define a function to execute for each case
    def caseTask():
        """ Task is to be synced and message to be sent to owner and collaborators """
        task = taskdal.getTaskById(syncObj)
        for coll in task.collaborators:
            if not coll.user.serverPushId in androidPayload:
                androidPayload.append(str(coll.user.serverPushId))
        androidPayload.append(str(task.owner.serverPushId))

    def caseGroup():
        """ Group is to be synced and message to be sent to all group members"""
        group = groupdal.getGroupById(syncObj)
        for member in group.members:
            if not member.serverPushId in androidPayload:
                androidPayload.append(str(member.serverPushId))
        androidPayload.append(str(group.owner.serverPushId))

    def caseBuzz():
        """Buzz all the collaborators of a task """
        task = taskdal.getTaskById(syncObj)
        for coll in task.collaborators:
            if not coll.user.serverPushId in androidPayload:
                androidPayload.append(str(coll.user.serverPushId))


    #Define the lookup dictionary
    choice = {"Task":caseTask, "Group":caseGroup, "Buzz":caseBuzz}

    choice[syncObj.datatype]() #to call appropriate case    
    

    if len(androidPayload) > 0:
        androidPush.payload[TOKEN_GCM_REGISTRATION_IDS] = androidPayload
        androidPush.payload["data"] = syncObj.to_dict()


        #Create UrlPoster Thread for GCM Push Start Thread
        gcmPostThread = UrlPostThread(
                                    threadID = 1,
                                    name = 'gcmPostThread',
                                    postObj = androidPush
                                    )
        gcmPostThread.start()