/**
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Forcible Life360-User
 *
 *  A mashup of the Life360-User and the forcible-mobile-presence published by krlaframboise
 *  
 *  Author: DJ5483
 *  Date: 2017-07-22
 */
 
metadata {
	definition (name: "Forcible Life360 User", namespace: "dj5483", author: "Don Jones") {
		capability "Presence Sensor"
		capability "Sensor"
		
		command "arrived"
		command "departed"
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}

	tiles(scale: 2) {
		standardTile("presence", "device.presence", width: 4, height: 4, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#00A0DC")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
		}
		standardTile("setArrived", "generic", width: 2, height: 2) {
			state "default", label:'Arrive', action:"arrived"
		}
		standardTile("setDeparted", "generic", width: 2, height: 2) {
			state "default", label:'Depart', action:"departed"
		}

		main "presence"
		details(["presence", "setArrived", "setDeparted"])
	}
}

def generatePresenceEvent(boolean present) {
	log.info "Life360 generatePresenceEvent($present)"
	def value = formatValue(present)
	def linkText = getLinkText(device)
	def descriptionText = formatDescriptionText(linkText, present)
	def handlerName = getState(present)

	def results = [
		name: "presence",
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName
	]
	log.debug "Generating Event: ${results}"
	sendEvent (results)
}

def setMemberId (String memberId) {
   log.debug "MemberId = ${memberId}"
   state.life360MemberId = memberId
}

def getMemberId () {

	log.debug "MemberId = ${state.life360MemberId}"
	
	return(state.life360MemberId)
}

private String formatValue(boolean present) {
	if (present)
		return "present"
	else
		return "not present"
}

private formatDescriptionText(String linkText, boolean present) {
	if (present)
		return "Life360 User $linkText has arrived"
	else
		return "Life360 User $linkText has left"
}

private getState(boolean present) {
	if (present)
		return "arrived"
	else
		return "left"
}

def arrived() {
	sendForcedEvent("present")
}

def departed() {
	sendForcedEvent("not present")
}

private sendForcedEvent(newState) {
	def displayState = (newState == "present") ? "Present" : "Not Present"
	sendEvent(name: "presence", value: newState, descriptionText: "${device.displayName} was forced to ${displayState}", isStateChange: true)
}
