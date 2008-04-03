project(wcmetric)

find_package(Boost REQUIRED)

Find_Package(Qt4 REQUIRED) # find and setup Qt4 for this project
set(QT_USE_QTGUI FALSE)
include (${QT_USE_FILE})

set ( SRC_FILES
     main.cpp
     wcmetric.cpp
     wcmetricjob.cpp
  )

set ( HEADER_FILES
    wcmetric.h
    wcmetricjob.h
  )

set (MOC_HEADERS
  )

include_directories(../../include)

add_executable( wcmetric ${SRC_FILES}  ${HEADER_FILES} )

QT4_WRAP_CPP(MOC_SRCS ${MOC_HEADERS})

target_link_libraries( wcmetric
  ${QT_LIBRARIES}
  -L../../lib -lalitheia
  -lmico2.3.12 -lmicocoss2.3.12
  -lssl -lcrypto
  -lboost_thread-mt
  -lboost_date_time
)
