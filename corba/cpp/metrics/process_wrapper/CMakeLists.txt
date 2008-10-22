cmake_minimum_required( VERSION 2.6 )

find_package( Boost REQUIRED )

set( SRC_FILES
     main.cpp
     wrappermetric.cpp
     wrappermetricjob.cpp
     temporaryfile.cpp
  )

set( HEADER_FILES
    wrappermetric.h
    wrappermetricjob.h
    temporaryfile.h
  )

include_directories( ../../include . )
link_directories( ${Boost_LIBRARY_DIRS} )

add_executable( process_wrapper ${SRC_FILES}  ${HEADER_FILES} )


target_link_libraries( process_wrapper
  alitheia
  ${MICO_LIBRARIES}
  boost_thread-mt
  boost_date_time
  boost_program_options
  boost_filesystem
)
