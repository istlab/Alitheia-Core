# Finds MICO libraries

if( MICO_INCLUDE_DIR AND MICO_LIBRARIES )
    set( MICO_FIND_QUIETLY TRUE )
endif( MICO_INCLUDE_DIR AND MICO_LIBRARIES )

find_package( OpenSSL REQUIRED )

find_path( MICO_INCLUDE_DIR mico/config.h )

find_library( MICO_LIBRARY NAMES mico2.3.12
                                 mico2.3.13 )

find_library( MICOCOSS_LIBRARY NAMES micocoss2.3.12
                                     micocoss2.3.13 )

find_program( MICO_IDL_COMPILER idl )

set( MICO_LIBRARIES ${MICO_LIBRARY} 
                    ${MICOCOSS_LIBRARY} 
                    ${OPENSSL_LIBRARIES}
     CACHE LIST "MICO libraries" )

if( MICO_INCLUDE_DIR AND MICO_LIBRARY AND MICOCOSS_LIBRARY )
    set( MICO_FOUND TRUE )
else( MICO_INCLUDE_DIR AND MICO_LIBRARY AND MICOCOSS_LIBRARY )
    set( MICO_FOUND FALSE )
endif( MICO_INCLUDE_DIR AND MICO_LIBRARY AND MICOCOSS_LIBRARY )

if( MICO_FOUND )
    if( NOT MICO_FIND_QUIETLY )
        message( STATUS "Found MICO: ${MICO_LIBRARY} ${MICOCOSS_LIBRARY}" )
    endif( NOT MICO_FIND_QUIETLY )
else( MICO_FOUND )
    if( MICO_FIND_REQUIRED )
        message( FATAL_ERROR "Could NOT find MICO" )
    endif( MICO_FIND_REQUIRED )
endif( MICO_FOUND )
