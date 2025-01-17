/*
 * Copyright 2009-2015 DigitalGlobe, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mrgeo.resources.job;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class JobStateResponse
{
  private Date _startTime; //time when job was started
  private long _duration; //duration of the jib in ms from start to completion
  private float _percent;
  private String _message;
  private String _state;
  private boolean _isFinished;
  public void setStartTime(Date startTime)
  {
    this._startTime = startTime;
  }

  public Date getStartTime()
  {
    return _startTime;
  }
  
  public void setPercent(float percent)
  {
    this._percent = percent;
  }

  public float getPercent()
  {
    return _percent;
  } 
  public void setState(String state)
  {
    this._state = state;
  }

  public String getState()
  {
    return _state;
  }
  public void setDuration(long duration)
  {
    this._duration = duration;
  }

  public long getDuration()
  {
    return _duration;
  }
  
  public String getMessage() {
    return _message;
  }
  
  public void setMessage(String msg) {
    _message = msg;
  }
  
  public boolean getJobFinished() {
    return _isFinished;
  }
  
  public void setJobFinished(boolean f) {
    _isFinished = f;
  }  
  
}
