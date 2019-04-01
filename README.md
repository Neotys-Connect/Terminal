<p align="center"><img src="/screenshots/ascii-icon.png" width="40%" alt="rte Logo" /></p>

# RTE support for  for NeoLoad

## Overview

These Advanced Actions allows you to generate RTE traffic with [NeoLoad](https://www.neotys.com/neoload/overview) .

This bundle provides RTE supports for SSH and Telnet  
### Ssh support of Manhatan
* **OpenSession:**
  This action will open the ssh connection to a specific host.

* **SendSpecialKey:**
  This action is sending Special Keys to the openned  ssh connectiong .
  Here is the list of the special Keys supported :
  * CR
  * ESC
  * DEL
  * BS
  * HT
  * LF
  * VT
  * CTRLA
  * CTRLB
  * CTRLC
  * CTRLD
  * CTRLE
  * CTRLF
  * CTRLG
  * CTRLH
  * CTRLI
  * CTRLJ
  * CTRLK
  * CTRLL
  * CTRLM
  * CTRLN
  * CTRLO
  * CTRLP
  * CTRLQ
  * CTRLR
  * CTRLS
  * CTRLT
  * CTRLU
  * CTRLV
  * CTRLW
  * CTRLX
  * CTRLY
  * CTRLZ
  * UP
  * DOWN
  * LEFT
  * RIGHT
  * CRLF
  
* **SendKey:**
  This action is sending a key or set of keys ( a-Z, 0-9 and others characters) to the openned ssh connection.
  
 * **SendKeyAndWaitFor:**
   This action is sending a key or set of keys to the openned ssh connection and and is waiting to receive a special pattern from the server.

* **SendSpecialKeyAndWaitFor:**
 This action is sending Special Keys to the openned  ssh connectiong  and is waiting to receive a special pattern from the server.
   
* **ReadActionUntil:**
  This action is reading until receiving a specific pattern

* **CloseSession:**
   This action will close the ssh connection to a specific host.

### Telnet support 
* **OpenTelnetSession:**
  This action will open the telnet connection to a specific host.

* **SendTelnetSpecialKey:**
  This action is sending Special Keys to the openned  ssh connectiong .
  Here is the list of the special Keys supported :
  * CR
  * ESC
  * DEL
  * BS
  * HT
  * LF
  * VT
  * CTRLA
  * CTRLB
  * CTRLC
  * CTRLD
  * CTRLE
  * CTRLF
  * CTRLG
  * CTRLH
  * CTRLI
  * CTRLJ
  * CTRLK
  * CTRLL
  * CTRLM
  * CTRLN
  * CTRLO
  * CTRLP
  * CTRLQ
  * CTRLR
  * CTRLS
  * CTRLT
  * CTRLU
  * CTRLV
  * CTRLW
  * CTRLX
  * CTRLY
  * CTRLZ
  * UP
  * DOWN
  * LEFT
  * RIGHT
  * CRLF
  
* **SendTelnetKey:**
  This action is sending a key or set of keys ( a-Z, 0-9 and others characters) to the openned ssh connection.
  
 * **SendTelnetKeyAndWaitFor:**
   This action is sending a key or set of keys to the openned ssh connection and and is waiting to receive a special pattern from the server.

* **SendTelnetSpecialKeyAndWaitFor:**
 This action is sending Special Keys to the openned  ssh connectiong  and is waiting to receive a special pattern from the server.

   
* **ReadTelnetActionUntil:**
  This action is reading until receiving a specific pattern
    
     

## Installation

1. Download the [latest release](https://github.com/Neotyslab/TerminalEmulator/releases/latest) for NeoLoad from version 6.7 or this [release](https://github.com/Neotys-Labs/Dynatrace/releases/tag/Neotys-Labs%2FDynatrace.git-2.0.10) for previous NeoLoad versions.
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm).

<p align="center"><img src="/screenshots/rte_actions.png" alt="Terminal Emulator Advanced Action" /></p>

## NeoLoad Set-up

Once installed, how to use in a given NeoLoad project:

1. Create a “UserPath X” User Path.
1. Insert “OpenSession” or "OpenTelnetSession" in the 'Init' block or as the first action of the UserPath.
1. Insert "CloseSession" or "CloseTelnetSession" in the ‘End’ block or as the last action of the UserPath.
  <p align="center"><img src="/screenshots/rte_vu.png" alt="Rte User Path" /></p>

   
## Parameters for  OpenSession 
   
| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the ssh server|
| Port  |  Port of of ssh server |
| UserName  |  login to connect to the ssh server |
| Password |  password to connect o the ssh server |
| TimeOut  | Timeout of the ssh connection |
| EnablePtty  (Optional) | change the output of the ssh connection . gives more details in case of server errors |

## Parameters for  OpenTelnetSession 
   
| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the telnet server|
| Port  |  Port of of telnet server |
| TerminalType  |  Type of terninal : VT100, VT200..Etc |
| TimeOut  | Timeout (in s) of the telnet connection |
  
## Parameters for ReadUntil and ReadTelnetUntil
   
| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the  server |
| CHECK1  |  Pattern 1 that the action will determine the last character of the expected screen |
| CHECK2  (Optional)|  Pattern 2 that the action will determine the last character of the expected screen|
| ..CHECKX (Optional)|   Pattern X that the action will determine the last character of the expected screen|
| OPERATOR (Optional) | OPERATOR that would be applied between CHECK1, CHECK2, ...Etc . Value Possible :<ul><li>AND</li><li>OR</li></ul> |
| TimeOut  | Timeout (in s) to find the pattern(s) |

## Parameters for SendSpecialKeyAndWaitFor and SendTelnetSpecialKeyAndWaitFor
   
| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the  server |
| KEY      | Special Key to send . Value posible : <ul><li>CR</li><li>ESC</li><li>DEL</li><li>BS</li><li>HT</li><li>LF</li><li>VT</li><li>CTRLA</li><li>CTRLB</li><li>CTRLC</li><li>CTRLD</li><li>CTRLE</li><li>CTRLF</li><li>CTRLG</li><li>CTRLH</li><li>CTRLI</li><li>CTRLJ</li><li>CTRLK</li><li>CTRLL</li><li>CTRLM</li><li>CTRLN</li><li>CTRLO</li><li>CTRLP</li><li>CTRLQ</li><li>CTRLR</li><li>CTRLS</li><li>CTRLT</li><li>CTRLU</li><li>CTRLV</li><li>CTRLW</li><li>CTRLX</li><li>CTRLY</li><li>CTRLZ</li><li>UP</li><li>DOWN</li><li>LEFT</li><li>RIGHT</li><li>CRLF</li></ul>|
| CHECK1  |  Pattern 1 that the action will determine the last character of the expected screen |
| CHECK2  (Optional)|  Pattern 2 that the action will determine the last character of the expected screen|
| ..CHECKX (Optional)|   Pattern X that the action will determine the last character of the expected screen|
| OPERATOR (Optional) | OPERATOR that would be applied between CHECK1, CHECK2, ...Etc . Value Possible :<ul><li>AND</li><li>OR</li></ul> |
| TimeOut  | Timeout (in s) to find the pattern(s) |

## Parameters for SendSpecialKey and SendTelnetSpecialKey

| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the  server |
| KEY      | Special Key to send . Value posible : <ul><li>CR</li><li>ESC</li><li>DEL</li><li>BS</li><li>HT</li><li>LF</li><li>VT</li><li>CTRLA</li><li>CTRLB</li><li>CTRLC</li><li>CTRLD</li><li>CTRLE</li><li>CTRLF</li><li>CTRLG</li><li>CTRLH</li><li>CTRLI</li><li>CTRLJ</li><li>CTRLK</li><li>CTRLL</li><li>CTRLM</li><li>CTRLN</li><li>CTRLO</li><li>CTRLP</li><li>CTRLQ</li><li>CTRLR</li><li>CTRLS</li><li>CTRLT</li><li>CTRLU</li><li>CTRLV</li><li>CTRLW</li><li>CTRLX</li><li>CTRLY</li><li>CTRLZ</li><li>UP</li><li>DOWN</li><li>LEFT</li><li>RIGHT</li><li>CRLF</li></ul>|
| TimeOut  | Timeout (in s) to  send the specialKey |


## Parameters for SendKey and SendTelnetKey

| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the  server |
| KEY      |  Keys to send  (a-zA-Z0-9 and other characters)|
| NoWaitForEcho | the action won't wait to receive the confirmation from the server|
| TimeOut  | Timeout (in s) to receive the echo |

  
## Parameters for SendKeyandWaitFor and SendTelnetKeyandWaitFor

| Name             | Description |
| -----            | ----- |
| HOST      | Ip or Hostname of the  server |
| KEY      |  Keys to send  (a-zA-Z0-9 and other characters)|
| CHECK1  |  Pattern 1 that the action will determine the last character of the expected screen |
| CHECK2  (Optional)|  Pattern 2 that the action will determine the last character of the expected screen|
| ..CHECKX (Optional)|   Pattern X that the action will determine the last character of the expected screen|
| OPERATOR (Optional) | OPERATOR that would be applied between CHECK1, CHECK2, ...Etc . Value Possible :<ul><li>AND</li><li>OR</li></ul> |
| TimeOut  | Timeout (in s) to receive the pattern(s)|

 ## Parameters for  CloseSession and CloseTelnetSerssion
    
 | Name             | Description |
 | -----            | ----- |
 | HOST      | Ip or Hostname of the  server|



## Status Codes
  * SSH actions : 
    * NL-CloseSession_ERROR: Error during the ssh close action
    * NL-SendKeyAndWait_ERROR: Error during the ssh send keys and waitfor action
    * NL-SendKey_ERROR :Error during the ssh send keys 
    * NL-SendSpecialKeyAndWaitFor_ERROR: Error during the  ssh Send Special Key And Waitfor action
    * NL-SendSpecialKey_ERROR: Error during the ssh send special keys
    * NL-ReadActionUntil_ERROR: Error during the ssh read until action
    * NL-OpenSession_ERROR : Error during the ssh open session action
  * Telnet actions : 
    * NL-CloseTelnetSession_ERROR: Error during the telnet close action
    * NL-SendTelnetKeyAndWait_ERROR: Error during the telnet send keys and waitfor action
    * NL-SendTelnetKey_ERROR : Error during the telnet send keys
    * NL-SendTelnetSpecialKeyAndWaitFor_ERROR: Error during the  telnet Send Special Key And Waitfor action
    * NL-SendTelnetSpecialKey_ERROR: Error during the telnet send special keys
    * NL-ReadTelnetActionUntil_ERROR: Error during the telnet read until action
    * NL-OpenTelnetSessionAction_ERROR : Error during the telnet open session action


| Property | Value |
| -----| -------------- |
| Maturity | Experimental |
| Author   | Neotys Partner Team |
| License  | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad  | 6.3+ (Enterprise or Professional Edition w/ Integration & Advanced Usage and NeoLoad Web option required)|
| Requirements | NeoLoad Web |
| Bundled in NeoLoad | No
| Download Binaries | <ul><li>[latest release](https://github.com/Neotyslab/TerminalEmulator/releases/latest) is only compatible with NeoLoad from version 6.7</li><li> Use this [release](https://github.com/Neotys-Labs/Dynatrace/releases/tag/Neotys-Labs%2FDynatrace.git-2.0.10) for previous NeoLoad versions</li></ul>|
