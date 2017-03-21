/**
 *  Copyright 2015 SmartThings
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
 *  Delayed Entry
 * 
 *	Description: I wrote this to for entry delay in the built in home monitoring app by SmartThings. It simply monitors a contact and operates a switch
 *  to the on position after the specified delay. You can then choose to have it automatically reset or stay on if you like. The idea is to not monitor the
 * 	main contact in the Smart Home Monitor, but rather the follower (usually a virtual switch) instead. That way if you open your front door, your alarm doesn't
 *  go off immediately, you can set yourself up a reminder to disarm the alarm when you open the door in away mode. That way you have sometime to keep that siren
 *  from waking the neighbors.
 *
 *  Author: Garrett Hensley (packerprogrammer)
 */

definition(
    name: "${appName()}",
    namespace: "packerprogrammer",
    author: "Garrett Hensley",
    description: "When an input device is activated (i.e. contact open) another switch is turned on after a pre-defined delay (typically a virtual switch). It can be turned off " +
    				"automatically as well.",
    category: "Convenience",
    singleInstance: true,
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	page(name: "startPage")
    page(name: "parentPage")
    page(name: "childStartPage")
}
	
    
def startPage() {
    if (parent) {
        childStartPage()
    } else {
        parentPage()
    }
} 	
    
    
def parentPage() {
	return dynamicPage(name: "parentPage", title: "Delayed Entry Routines", nextPage: "", install: false, uninstall: true) {
        section() {
            app(name: "childApps", appName: appName(), namespace: "packerprogrammer", title: "New Delayed Entry", multiple: true)
        }
    }
}
def childStartPage() {
	return dynamicPage(name: "childStartPage", title: "Create Delayed Entry", install: true, uninstall: false) { 
    	section("Setup Entry Delay"){
			input "contactMaster", "capability.contactSensor", title: "Master Contact Sensor"
        	input "switches", "capability.switch", title: "Switch to Control", multiple: false
        	input "onDelay", "bool", title: "Delay On?", defaultValue: false, required: false 
        	input "delayOn", "number", title: "Seconds to Delay On", defaultValue: 120, required: false
    	}
    	section(hideable:true, hidden:true, title:"(optional) Auto Off Settings") {
    		input "autoOff", "bool", title: "Auto Off?", defaultValue: false, required: false
        	input "delayOff", "number", title: "Seconds to Delay Off",defaultValue: 1, required: false, hideWhenFalse:"autoOff"	
    	}
         section("Setting") {
        	label(title: "Assign a name", required: false)
        }
    }
}
        
	
    
private def appName() { return "${parent ? "The Child" : "Delayed Entry"}" }

def installed() {
	log.debug "Begin Installed."
	initialization()     	
    log.debug "End Installed"
}

def updated() {
	log.debug "Begin Updated."
	unsubscribe()
    initialization()
    log.debug "End Updated."
}

def initialization() {
	log.debug "parent = " + parent
    if(parent) { 
    	initChild() 
    } else {
    	initParent() 
    } 
}


def initParent() {
	log.debug "Init Parent."
}

def initChild() {
	log.debug "Init Child."
	subscribe(contactMaster, "contact", contactHandler)
    switches.off()
}

def contactHandler(evt) {
	log.debug "$evt.name: $evt.value"
	if (evt.value == "open") {
		log.debug "start timing"
        runIn(delayOn, operateSwitches, [overwrite:false])
	} else if (evt.value == "closed") {
    	
	}
}

def operateSwitches() {
	log.debug "turn on switches"
    switches.on()
    log.debug "autOff = " + autoOff
    if (autoOff == true) {
    	log.debug "turn off switches (automatic)"
    	switches.off([delay: (delayOff + 1) * 1000])
    }    
}