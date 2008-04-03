project(process_wrapper)

find_package(Boost REQUIRED)

Find_Package(Qt4 REQUIRED) # find and setup Qt4 for this project
set(QT_USE_QTGUI FALSE)
include (${QT_USE_FILE})

set ( SRC_FILES
     main.cpp
     wrappermetric.cpp
     wrappermetricjob.cpp
  )

set ( HEADER_FILES
    wrappermetric.h
    wrappermetricjob.h
  )

set (MOC_HEADERS
    wrappermetricjob.h
  )

include_directories(../../include)

QT4_WRAP_CPP(MOC_SRCS ${MOC_HEADERS})
add_executable( process_wrapper ${SRC_FILES}  ${HEADER_FILES} ${MOC_SRCS} )


target_link_libraries( process_wrapper
  ${QT_LIBRARIES}
  -L../../lib -lalitheia
  -lmico2.3.12 -lmicocoss2.3.12
  -lssl -lcrypto
  -lboost_thread-mt
  -lboost_date_time
)
