# uploadArchives默认按Release渠道
./gradlew :butterknife-annotations:clean :butterknife-annotations:uploadArchives
./gradlew :butterknife-runtime:clean :butterknife-runtime:uploadArchives
./gradlew :butterknife:clean :butterknife:assembleRemoteRelease :butterknife:uploadArchives
./gradlew :butterknife-compiler:clean :butterknife-compiler:uploadArchives
