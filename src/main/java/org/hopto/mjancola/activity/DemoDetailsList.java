/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hopto.mjancola.activity;

import org.hopto.mjancola.R;

/**
 * A list of all the demos we have available.
 */
public final class DemoDetailsList {

    /** This class should not be instantiated. */
    private DemoDetailsList() {}

    public static final DemoDetails[] DEMOS = {
    new DemoDetails( R.string.my_location_demo_label,
                     R.string.my_location_demo_description,
                     MyLocationDemoActivity.class ),
    new DemoDetails( R.string.activity_recognition_label,
                     R.string.activity_recognition_description,
                     ActivityRecognitionActivity.class ),
    new DemoDetails( R.string.speed_detection_label ,
                     R.string.speed_detection_description,
                     SpeedActivity.class)
    };
}

