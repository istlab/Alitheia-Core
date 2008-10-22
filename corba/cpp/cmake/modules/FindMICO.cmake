# Finds MICO libraries

find_package( OpenSSL REQUIRED )

find_path( MICO_INCLUDE_DIR mico/config.h )

find_library( MICO_LIBRARY NAMES mico2.3.12
                                 mico2.3.13 )

find_library( MICOCOSS_LIBRARY NAMES micocoss2.3.12
                                     micocoss2.3.13 )

set( MICO_LIBRARIES ${MICO_LIBRARY} 
                    ${MICOCOSS_LIBRARY} 
                    ${OPENSSL_LIBRARIES}
     CACHE LIST "MICO libraries" )
