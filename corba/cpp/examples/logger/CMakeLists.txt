cmake_minimum_required( VERSION 2.6 )

find_package( Boost REQUIRED )
find_package( MICO REQUIRED )

set ( SRC_FILES
     main.cpp
  )

include_directories( ${MICO_INCLUDE_DIR} ../../include .)
link_directories( ${Boost_LIBRARY_DIRS})

add_executable( logger ${SRC_FILES} )

target_link_libraries( logger
    alitheia
    ${MICO_LIBRARIES}
    boost_thread-mt
    boost_date_time
    boost_filesystem
)
