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

include_directories(../../include .)

add_executable( process_wrapper ${SRC_FILES}  ${HEADER_FILES} ${MOC_SRCS} )


target_link_libraries( process_wrapper
  -L../../lib -lalitheia
  -lmico2.3.12 -lmicocoss2.3.12
  -lssl -lcrypto
  -lboost_thread-mt
  -lboost_date_time
  -lboost_program_options
)
