# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


include_directories(C:/opencv-4.1.2-android-sdk/OpenCV-android-sdk/sdk/native/jni/include)

add_library( # Sets the name of the library.
        DetectionBasedTracker_jni

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        DetectionBasedTracker_jni.cpp )



find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )


target_link_libraries( # Specifies the target library.
        DetectionBasedTracker_jni

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )