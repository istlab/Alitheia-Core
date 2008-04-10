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
//! \file boost/process/win32_launcher.hpp
//!
//! Includes the declaration of the win32_launcher class.
//!

#if !defined(BOOST_PROCESS_WIN32_LAUNCHER_HPP)
/** \cond */
#define BOOST_PROCESS_WIN32_LAUNCHER_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if !defined(BOOST_PROCESS_WIN32_API)
#   error "Unsupported platform."
#endif

extern "C" {
#include <windows.h>
}

#include <boost/assert.hpp>
#include <boost/process/detail/factories.hpp>
#include <boost/process/detail/file_handle.hpp>
#include <boost/process/launcher.hpp>
#include <boost/process/win32_child.hpp>
#include <boost/scoped_ptr.hpp>

namespace boost {
namespace process {

// ------------------------------------------------------------------------

//!
//! \brief Generic implementation of the Launcher concept.
//!
//! The launcher class implements the Launcher concept in an operating
//! system agnostic way; it allows spawning new child process using a
//! single and common interface across different systems.
//!
class win32_launcher :
    public launcher
{
    //!
    //! \brief Win32-specific process startup information.
    //!
    boost::scoped_ptr< STARTUPINFO > m_startupinfo;

public:
    //!
    //! \brief Constructs a new launcher.
    //!
    //! Constructs a new Win32-specific launcher.  If \a si is not a null
    //! pointer, the provided information is used to start the new process;
    //! otherwise the default settings are used (i.e. an empty STARTUPINFO
    //! object).
    //!
    //! \pre The \a si object \a cb field must be equal to or greater than
    //!      sizeof(STARTUPINFO).
    //! \pre The \a si object cannot have the STARTF_USESTDHANDLES flag set
    //!      in the \a dwFlags field because the communication handles are
    //!      initialized by the launcher.
    explicit win32_launcher(const STARTUPINFO* si = NULL);

    //!
    //! \brief Starts a new child process.
    //!
    //! Given a command line \a cl, starts a new process with all the
    //! parameters configured in the launcher.  The launcher can be
    //! reused afterwards to launch other different command lines.
    //!
    //! \remark <b>Blocking remarks</b>: This function may block if the
    //!         device holding the command line's executable blocks when
    //!         loading the image.  This might happen if, e.g., the binary
    //!         is being loaded from a network share.
    //!
    //! \return A handle to the new child process.
    //!
    template< class Command_Line >
    win32_child start(const Command_Line& cl);
};

// ------------------------------------------------------------------------

inline
win32_launcher::win32_launcher(const STARTUPINFO* si)
{
    if (si != NULL) {
        BOOST_ASSERT(si->cb >= sizeof(STARTUPINFO));
        BOOST_ASSERT(!(si->dwFlags & STARTF_USESTDHANDLES));

        // XXX I'm not sure this usage of scoped_ptr is correct...
        m_startupinfo.reset((STARTUPINFO*)new char[si->cb]);
        ::CopyMemory(m_startupinfo.get(), si, si->cb);
    } else {
        m_startupinfo.reset(new STARTUPINFO);
        ::ZeroMemory(m_startupinfo.get(), sizeof(STARTUPINFO));
        m_startupinfo->cb = sizeof(STARTUPINFO);
    }
}

// ------------------------------------------------------------------------

template< class Command_Line >
inline
win32_child
win32_launcher::start(const Command_Line& cl)
{
    detail::file_handle fhstdin, fhstdout, fhstderr;

    detail::stream_info behin =
        win32_behavior_to_info(get_stdin_behavior(), false, fhstdin);
    detail::stream_info behout =
        win32_behavior_to_info(get_stdout_behavior(), true, fhstdout);
    detail::stream_info beherr =
        win32_behavior_to_info(get_stderr_behavior(), true, fhstderr);

    detail::win32_setup s;
    s.m_work_directory = get_work_directory();
    s.m_startupinfo = m_startupinfo.get();

    PROCESS_INFORMATION pi = detail::win32_start(cl, get_environment(),
                                                 behin, behout, beherr,
                                                 get_merge_out_err(), s);

    return detail::factories::create_win32_child(pi, fhstdin, fhstdout,
                                                 fhstderr);
}

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_WIN32_LAUNCHER_HPP)
