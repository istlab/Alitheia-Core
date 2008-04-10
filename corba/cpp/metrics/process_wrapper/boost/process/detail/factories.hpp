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
//! \file boost/process/detail/factories.hpp
//!
//! Includes the declaration of several private factories to construct
//! objects on behalf of other classes in the library.  This is to prevent
//! the user from instantiating them on his own.
//!

#if !defined(BOOST_PROCESS_DETAIL_FACTORIES_HPP)
/** \cond */
#define BOOST_PROCESS_DETAIL_FACTORIES_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
#   include <boost/process/posix_child.hpp>
#elif defined(BOOST_PROCESS_WIN32_API)
extern "C" {
#   include <windows.h>
}
#   include <boost/process/win32_child.hpp>
#else
#   error "Unsupported platform."
#endif

#include <boost/process/child.hpp>
#include <boost/process/detail/file_handle.hpp>

namespace boost {
namespace process {
namespace detail {

struct factories {

// ------------------------------------------------------------------------

//!
//! \brief Creates a new Child instance.
//!
//! Creates a new instance of the child class.  See its constructor for
//! more details on the parameters.
//!
static inline
child
create_child(child::handle_type h,
             file_handle fhstdin,
             file_handle fhstdout,
             file_handle fhstderr)
{
    return child(h, fhstdin, fhstdout, fhstderr);
}

// ------------------------------------------------------------------------

#if defined(BOOST_PROCESS_POSIX_API)
//!
//! \brief Creates a new POSIX Child instance.
//!
//! Creates a new instance of the posix_child class.  See its constructor
//! for more details on the parameters.
//!
static inline
posix_child
create_posix_child(posix_child::handle_type h,
                   detail::info_map& infoin,
                   detail::info_map& infoout)
{
    return posix_child(h, infoin, infoout);
}
#endif

// ------------------------------------------------------------------------

#if defined(BOOST_PROCESS_WIN32_API)
//!
//! \brief Creates a new Win32 Child instance.
//!
//! Creates a new instance of the win32_child class.  See its constructor
//! for more details on the parameters.
//!
static inline
win32_child
create_win32_child(const PROCESS_INFORMATION& pi,
                   file_handle fhstdin,
                   file_handle fhstdout,
                   file_handle fhstderr)
{
    return win32_child(pi, fhstdin, fhstdout, fhstderr);
}
#endif

// ------------------------------------------------------------------------

}; // struct factories

} // namespace detail
} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_DETAIL_FACTORIES_HPP)
