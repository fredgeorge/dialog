# Dialog FLow

## Purpose

This document describes the interactions between a Web client and the
dialog backend.

## Flows

1. Client sends a __login__ request without any identification of a prior
 _Conversation_. Response will be an __issues list__ reply. This starts
 a new _Conversation_.
2. Client sends a __login__ request with a _Conversation_ ID to resume a
 previous conversation. If there are multiple outstanding _Issues_, response
 will be an __issues list__ reply. If there is a single outstanding Issue
 with a corresponding _Dialog_, and that dialog is incomplete, a __question__
 reply is send. Otherwise, the reply is the same as for __all issues__ request.
3. Client sends an __answer__ request containing the _Answer_ to a _Question_
 for a _Dialog_ with a _Conversation_ ID. If there is another _Question_ for
 that _Dialog_, the response will be another __question__ reply. If this 
 _Dialog_ is resolved, the remaining _Issues_ are examined, the the reply logic
 is the same as for __login__ request with a _Conversation_ ID.
4. Client sends a __refresh__ request. The reply will always be an __issues list__
 of outstanding open Issues even if there is only one Issue. 
 This request can occur at any time. It 
 allows the client to work on another available _Issue_ without completing the
 current Issue.
5. Client sends an __all issues__ request. This always returns all the current
 _Issues_ and the resolved _Issues_ in an expanded __issues list__ reply. This
 enables the client to change their mind on a prior _Answer_ to a _Question_.
 
## Other Information

If a response includes a _Conversation_ ID and optionally a _Dialog_ ID, these
should be included on subsequent requests from the Web client.

There is always an option to request a __refresh__. This suspends the current
_Dialog_, if any.

When presenting an __issues list__ reply, the status of the corresponding _Dialog_
ia also shown (Resolved Successfully, In Progress, Not Started, Resolved with Problems).
