
# TODO: https://www.codeforests.com/2020/06/04/python-to-read-email-from-outlook/
# TODO: https://www.codeforests.com/2021/05/16/python-reading-email-from-outlook-2/
# TODO: https://learn.microsoft.com/en-us/dotnet/api/microsoft.office.interop.outlook.mailitem?redirectedfrom=MSDN&view=outlook-pia#properties_

import os
import win32com.client

outlook = win32com.client.Dispatch('outlook.application')
mapi = outlook.GetNamespace("MAPI")

for account in mapi.Accounts:
    print(account.DeliveryStore.DisplayName)

inbox = mapi.GetDefaultFolder(6)
print(inbox)

folders = mapi.GetDefaultFolder(6).Folders
print(folders)

for folder in folders:
    print("\t", folder)

my_folder = mapi.GetDefaultFolder(6).Folders["! Инфоком.Адм.DevOps"]
print(my_folder)

# ['Actions', 'AddBusinessCard', 'AddRef', 'AlternateRecipientAllowed', 'Application', 'Attachments', 'AutoForwarded', 'AutoResolvedWinner', 'BCC', 'BillingInformation', 'Body', 'BodyFormat', 'CC', 'Categories', 'Class', 'ClearConversationIndex', 'ClearTaskFlag', 'Close', 'Companies', 'Conflicts', 'ConversationID', 'ConversationIndex', 'ConversationTopic', 'Copy', 'CreationTime', 'DeferredDeliveryTime', 'Delete', 'DeleteAfterSubmit', 'Display', 'DownloadState', 'EnableSharedAttachments', 'EntryID', 'ExpiryTime', 
# 'FlagDueBy', 'FlagIcon', 'FlagRequest', 'FlagStatus', 'FormDescription', 'Forward', 'GetConversation', 'GetIDsOfNames', 'GetInspector', 'GetTypeInfo', 'GetTypeInfoCount', 'HTMLBody', 'HasCoverSheet', 'Importance', 'InternetCodepage', 'Invoke', 'IsConflict', 
# 'IsIPFax', 'IsMarkedAsTask', 'ItemProperties', 'LastModificationTime', 'Links', 'MAPIOBJECT', 'MarkAsTask', 'MarkForDownload', 'MessageClass', 'Mileage', 'Move', 'NoAging', 'OriginatorDeliveryReportRequested', 'OutlookInternalVersion', 'OutlookVersion', 'Parent', 'Permission', 'PermissionService', 'PermissionTemplateGuid', 'PrintOut', 'PropertyAccessor', 'QueryInterface', 'RTFBody', 'ReadReceiptRequested', 'ReceivedByEntryID', 'ReceivedByName', 'ReceivedOnBehalfOfEntryID', 'ReceivedOnBehalfOfName', 'ReceivedTime', 'RecipientReassignmentProhibited', 'Recipients', 'Release', 'ReminderOverrideDefault', 'ReminderPlaySound', 'ReminderSet', 'ReminderSoundFile', 'ReminderTime', 'RemoteStatus', 'Reply', 'ReplyAll', 'ReplyRecipientNames', 'ReplyRecipients', 'RetentionExpirationDate', 'RetentionPolicyName', 'Save', 'SaveAs', 'SaveSentMessageFolder', 'Saved', 'Send', 'SendUsingAccount', 'Sender', 'SenderEmailAddress', 'SenderEmailType', 'SenderName', 'Sensitivity', 'Sent', 'SentOn', 'SentOnBehalfOfName', 'Session', 'ShowCategoriesDialog', 'Size', 'Subject', 'Submitted', 'TaskCompletedDate', 'TaskDueDate', 'TaskStartDate', 'TaskSubject', 'To', 'ToDoTaskOrdinal', 'UnRead', 'UserProperties', 'VotingOptions', 'VotingResponse', '_ApplyTypes_', '_FlagAsMethod', '_LazyAddAttr_', '_NewEnum', '_Release_', '_UpdateWithITypeInfo_', '__AttrToID__', '__LazyMap__', '__bool__', '__call__', '__class__', '__delattr__', '__dict__', '__dir__', '__doc__', '__eq__', '__format__', '__ge__', '__getattr__', '__getattribute__', '__getitem__', '__gt__', '__hash__', '__init__', '__init_subclass__', '__int__', '__le__', '__len__', '__lt__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__setitem__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_builtMethods_', '_dir_ole_', '_enum_', '_find_dispatch_type_', '_get_good_object_', '_get_good_single_object_', '_lazydata_', '_make_method_', '_mapCachedItems_', '_oleobj_', '_olerepr_', '_print_details_', '_proc_', '_unicode_to_string_', '_username_', '_wrap_dispatch_']
messages = my_folder.Items
for message in messages:
    # print(dir(message))
    print('Subject:', message.Subject)
