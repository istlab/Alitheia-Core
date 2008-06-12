project(process_wrapper)

find_package(Boost REQUIRED)

set ( SRC_FILES
     main.cpp
     wrappermetric.cpp
     wrappermetricjob.cpp
     temporaryfile.cpp
  )

set ( HEADER_FILES
    wrappermetric.h
    wrappermetricjob.h
    temporaryfile.h
  )

include_directories(/opt/local/include ../../include .)
link_directories( ../../lib ${Boost_LIBRARY_DIRS})

add_executable( process_wrapper ${SRC_FILES}  ${HEADER_FILES} )


target_link_libraries( process_wrapper
  alitheia
  mico2.3.12 
  micocoss2.3.12
  ssl 
  crypto
  boost_thread-mt
  boost_date_time
  boost_program_options
  boost_filesystem
)
