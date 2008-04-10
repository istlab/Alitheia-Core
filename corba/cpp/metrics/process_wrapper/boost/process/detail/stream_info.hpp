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
//! \file boost/process/detail/stream_info.hpp
//!
//! Provides the definition of the stream_info structure.
//!

#if !defined(BOOST_PROCESS_DETAIL_STREAM_INFO_HPP)
/** \cond */
#define BOOST_PROCESS_DETAIL_STREAM_INFO_HPP
/** \endcond */

#include <string>

#include <boost/optional.hpp>
#include <boost/process/detail/file_handle.hpp>
#include <boost/process/detail/pipe.hpp>

namespace boost {
namespace process {
namespace detail {

// ------------------------------------------------------------------------

//!
//! \brief Configuration data for a file descriptor.
//!
//! This convenience structure provides a compact way to pass information
//! around on how to configure a file descriptor.  It is used in
//! conjunction with the info_map map to create an unidirectional
//! association between file descriptors and their configuration details.
//!
struct stream_info
{
    enum type { close, inherit, usefile, usehandle, usepipe } m_type;

    // Valid when m_type == usefile.
    std::string m_file;

    // Valid when m_type == usehandle.
    file_handle m_handle;

    // Valid when m_type == usepipe.
    boost::optional< pipe > m_pipe;
};

// ------------------------------------------------------------------------

} // namespace detail
} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_DETAIL_STREAM_INFO_HPP)
