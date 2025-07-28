# Install script for directory: D:/AndroidStudioProjects/blocktopograph/app/src/cpp

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "C:/Program Files (x86)/leveldb")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Debug")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "0")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "TRUE")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE STATIC_LIBRARY FILES "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/libleveldb.a")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include/leveldb" TYPE FILE FILES
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/c.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/cache.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/comparator.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/db.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/dumpfile.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/env.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/export.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/filter_policy.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/iterator.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/options.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/slice.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/status.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/table_builder.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/table.h"
    "D:/AndroidStudioProjects/blocktopograph/app/src/cpp/include/leveldb/write_batch.h"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb/leveldbTargets.cmake")
    file(DIFFERENT EXPORT_FILE_CHANGED FILES
         "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb/leveldbTargets.cmake"
         "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/CMakeFiles/Export/lib/cmake/leveldb/leveldbTargets.cmake")
    if(EXPORT_FILE_CHANGED)
      file(GLOB OLD_CONFIG_FILES "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb/leveldbTargets-*.cmake")
      if(OLD_CONFIG_FILES)
        message(STATUS "Old export file \"$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb/leveldbTargets.cmake\" will be replaced.  Removing files [${OLD_CONFIG_FILES}].")
        file(REMOVE ${OLD_CONFIG_FILES})
      endif()
    endif()
  endif()
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb" TYPE FILE FILES "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/CMakeFiles/Export/lib/cmake/leveldb/leveldbTargets.cmake")
  if("${CMAKE_INSTALL_CONFIG_NAME}" MATCHES "^([Dd][Ee][Bb][Uu][Gg])$")
    file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb" TYPE FILE FILES "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/CMakeFiles/Export/lib/cmake/leveldb/leveldbTargets-debug.cmake")
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/leveldb" TYPE FILE FILES
    "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/cmake/leveldbConfig.cmake"
    "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/cmake/leveldbConfigVersion.cmake"
    )
endif()

if(CMAKE_INSTALL_COMPONENT)
  set(CMAKE_INSTALL_MANIFEST "install_manifest_${CMAKE_INSTALL_COMPONENT}.txt")
else()
  set(CMAKE_INSTALL_MANIFEST "install_manifest.txt")
endif()

string(REPLACE ";" "\n" CMAKE_INSTALL_MANIFEST_CONTENT
       "${CMAKE_INSTALL_MANIFEST_FILES}")
file(WRITE "D:/AndroidStudioProjects/blocktopograph/app/.cxx/Debug/6k21714o/x86/${CMAKE_INSTALL_MANIFEST}"
     "${CMAKE_INSTALL_MANIFEST_CONTENT}")
