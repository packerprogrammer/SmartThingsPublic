/**
 *  Energy Saver
 *
 *  Copyright 2014 SmartThings
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
 */
definition(
    name: "Routine by Energy",
    namespace: "packerprogrammer",
    author: "Garrett Hensley",
	description: "Run routine base on energy use",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)
preferences {
    page(name: "selectActions")
}

def selectActions() {
    dynamicPage(name: "selectActions", title: "Select Hello Home Action to Execute", install: true, uninstall: true) {
    	section(title: "Name") {
        	label title: "Name this Routine", required: true, defaultValue: "Energy Saver"
        }
		section {
        input(name: "meter", type: "capability.powerMeter", title: "When This Power Meter...", required: true, multiple: false, description: null)
        
        input(name: "function", title: "Is Greater or Less Than...", type: "enum", required: true, multiple: false, description: "Tap to choose...", metadata:[values:[">","<"]])
        input(name: "threshold", type: "number", title: "This Amount...", required: true, description: "in either watts or kw.")
        
	}
    section {
    	input(name: "switches", type: "capability.switch", title: "Turn Off These Switches", required: false, multiple: true, description: null)
        // Routine based inputs
    }
        // get the available actions
            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
            // sort them alphabetically
            actions.sort()
                    section("Hello Home Actions") {
                            log.trace actions
                // use the actions as the options for an enum input
                input "action", "enum", title: "Select an action to execute", options: actions
                    }
            }
    }
}



def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(meter, "power", meterHandler)
}

def meterHandler(evt) {
    def meterValue = evt.value as double
    def thresholdValue = threshold as int
    if (settings.function == ">") {
    	if (meterValue > thresholdValue) {
	    	log.debug "${meter} reported energy consumption ${settings.function} ${threshold}. Turning off switches (if applicable) and executing routine ${settings.action}."
    		if (switches != null) {
        		log.debug "switches not null"
        		switches.off()
        	}
        	location.helloHome?.execute(settings.action)
    	}
   } else {
   		if (meterValue < thresholdValue) {
	    	log.debug "${meter} reported energy consumption ${settings.function} ${threshold}. Turning off switches (if applicable) and executing routine ${settings.action}."
    		if (switches != null) {
        		log.debug "switches not null"
        		switches.off()
        	}
        	location.helloHome?.execute(settings.action)
   		}
   }
}