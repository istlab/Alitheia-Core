project(wcmetric)

find_package(Boost REQUIRED)

set ( SRC_FILES
     main.cpp
     wcmetric.cpp
     wcmetricjob.cpp
  )

set ( HEADER_FILES
    wcmetric.h
    wcmetricjob.h
  )

include_directories(../../include .)
link_directories( ../../lib ${Boost_LIBRARY_DIRS})

add_executable( wcmetric ${SRC_FILES}  ${HEADER_FILES} )

target_link_libraries( wcmetric 
    alitheia
    mico2.3.12 
    micocoss2.3.12
    ssl
    crypto
    boost_thread-mt
    boost_date_time
    boost_filesystem
)
