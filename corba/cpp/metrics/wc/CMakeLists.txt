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

include_directories(../../include)

add_executable( wcmetric ${SRC_FILES}  ${HEADER_FILES} )

target_link_libraries( wcmetric
  -L../../lib -lalitheia
  -lmico2.3.12 -lmicocoss2.3.12
  -lssl -lcrypto
  -lboost_thread-mt
  -lboost_date_time
  -lboost_filesystem
)
