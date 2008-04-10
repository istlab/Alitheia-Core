//
// Boost.Process
//
// Copyright (c) 2006 Julio M. Merino Vidal.
//
// Distributed under the Boost Software License, Version 1.0.
// (See accompanying file LICENSE_1_0.txt or copy at
// http://www.boost.org/LICENSE_1_0.txt.)
//

//!
//! \file boost/process/forward_decls.hpp
//!
//! This header provides forward declarations for all public types
//! included in the library.  It is interesting to note that those types
//! that are specific to a given platform are only provided if the library
//! is being used in that same platform.
//!

#if !defined(BOOST_PROCESS_FORWARD_DECLS_HPP)
/** \cond */
#define BOOST_PROCESS_FORWARD_DECLS_HPP
/** \endcond */

#include <boost/process/config.hpp>

/** \cond */
namespace boost {
namespace process {

// ------------------------------------------------------------------------

template< class Command_Line >
class basic_pipeline;

class child;

class children;

class command_line;

class launcher;

class pistream;

class postream;

class status;

typedef basic_pipeline< command_line > pipeline;

#if defined(BOOST_PROCESS_POSIX_API)
class posix_child;

class posix_launcher;

class posix_status;
#elif defined(BOOST_PROCESS_WIN32_API)
class win32_child;

class win32_launcher;
#else
#   error "Unsupported platform."
#endif

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost
/** \endcond */

#endif // !defined(BOOST_PROCESS_FORWARD_DECLS_HPP)
