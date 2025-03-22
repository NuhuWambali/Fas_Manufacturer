Dear Developers,

This package is the Android Java SDK for PTS-2 device.
You can use Android Studio to build the library and the application.
Please use the documents "Android SDK for PTS-2 controller" and "jsonPTS protocol for PTS-2 controller" that describes the low level protocol for PTS2 device. 
This implementation was made close to it. 
In current Java implementation the some difference is that some optional fields can be not returned by request. 
To check it you need to call corresponding method. For example before call “getProductDensity()“ 
you can check that the field was filled using method “isProductDensitySet()“ etc.
Also you can look at C# and C++ implementations in case of need.

Kind Regards
PTS2 team