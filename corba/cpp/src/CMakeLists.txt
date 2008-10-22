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

include_directories( . ${MICO_INCLUDE_DIR} )

add_custom_command( OUTPUT Alitheia.cpp Alitheia.h
                    COMMAND idl --any --poa --c++-suffix cpp ../../idl/Alitheia.idl
                    DEPENDS ../../idl/Alitheia.idl )

add_library( alitheia STATIC ${SOURCES} ${HEADERS} )
