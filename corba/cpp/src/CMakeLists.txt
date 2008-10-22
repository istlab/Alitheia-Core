project( AlitheiaLib )

cmake_minimum_required( VERSION 2.6 )

find_package( MICO )

set( SOURCES
     Alitheia.cpp
     corbahandler.cpp
     core.cpp
     database.cpp
     dbobject.cpp
     fds.cpp
     job.cpp
     logger.cpp
     metric.cpp
     scheduler.cpp
)

set( HEADERS
     Alitheia.h
     corbahandler.h
     core.h
     database.h
     dbobject.h
     fds.h
     job.h
     logger.h
     metric.h
     scheduler.h
)

include_directories( . ${MICO_INCLUDE_DIR} ${AlitheiaLib_BINARY_DIR} )

add_custom_command( OUTPUT Alitheia.cpp Alitheia.h
                    COMMAND ${MICO_IDL_COMPILER} --any --poa --c++-suffix cpp ${CMAKE_CURRENT_SOURCE_DIR}/../../idl/Alitheia.idl
                    DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/../../idl/Alitheia.idl" )

add_library( alitheia STATIC ${SOURCES} ${HEADERS} )
