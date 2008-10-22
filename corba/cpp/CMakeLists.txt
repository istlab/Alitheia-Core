cmake_minimum_required( VERSION 2.6 )

set( CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake/modules" )
set( LIBRARY_OUTPUT_PATH "${CMAKE_CURRENT_SOURCE_DIR}/lib" )

add_subdirectory( src )
include_directories( ${AlitheiaLib_BINARY_DIR} )

add_subdirectory( examples )
add_subdirectory( metrics )
