cmake_minimum_required( VERSION 2.6 )

set( CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake/modules" )
set( LIBRARY_OUTPUT_PATH "${CMAKE_CURRENT_SOURCE_DIR}/lib" )

add_subdirectory( examples )
add_subdirectory( src )
add_subdirectory( metrics )
