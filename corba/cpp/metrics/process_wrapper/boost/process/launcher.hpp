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
//! \file boost/process/launcher.hpp
//!
//! Includes the declaration of the launcher class.
//!

#if !defined(BOOST_PROCESS_LAUNCHER_HPP)
/** \cond */
#define BOOST_PROCESS_LAUNCHER_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
#   include <boost/process/detail/posix_ops.hpp>
#elif defined(BOOST_PROCESS_WIN32_API)
#   include <windows.h>
#   include <boost/process/detail/win32_ops.hpp>
#else
#   error "Unsupported platform."
#endif

#include <boost/assert.hpp>
#include <boost/process/child.hpp>
#include <boost/process/detail/command_line_ops.hpp>
#include <boost/process/detail/factories.hpp>
#include <boost/process/detail/file_handle.hpp>
#include <boost/process/detail/launcher_base.hpp>
#include <boost/process/exceptions.hpp>
#include <boost/throw_exception.hpp>

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
class launcher :
    public detail::launcher_base
{
public:
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
    child start(const Command_Line& cl);
};

// ------------------------------------------------------------------------

template< class Command_Line >
inline
child
launcher::start(const Command_Line& cl)
{
    child::handle_type ph;
    detail::file_handle fhstdin, fhstdout, fhstderr;

#if defined(BOOST_PROCESS_POSIX_API)
    detail::info_map infoin, infoout;
    detail::merge_set merges;

    posix_behavior_to_info(get_stdin_behavior(),  STDIN_FILENO,  false,
                           infoin);
    posix_behavior_to_info(get_stdout_behavior(), STDOUT_FILENO, true,
                           infoout);
    posix_behavior_to_info(get_stderr_behavior(), STDERR_FILENO, true,
                           infoout);

    if (get_merge_out_err())
        merges.insert(std::pair< int, int >(STDERR_FILENO, STDOUT_FILENO));

    detail::posix_setup s;
    s.m_work_directory = get_work_directory();

    ph = detail::posix_start(cl, get_environment(), infoin, infoout,
                             merges, s);

    if (get_stdin_behavior() == redirect_stream)
        fhstdin = posix_info_locate_pipe(infoin, STDIN_FILENO, false);

    if (get_stdout_behavior() == redirect_stream)
        fhstdout = posix_info_locate_pipe(infoout, STDOUT_FILENO, true);

    if (get_stderr_behavior() == redirect_stream)
        fhstderr = posix_info_locate_pipe(infoout, STDERR_FILENO, true);
#elif defined(BOOST_PROCESS_WIN32_API)
    detail::stream_info behin =
        win32_behavior_to_info(get_stdin_behavior(), false, fhstdin);
    detail::stream_info behout =
        win32_behavior_to_info(get_stdout_behavior(), true, fhstdout);
    detail::stream_info beherr =
        win32_behavior_to_info(get_stderr_behavior(), true, fhstderr);

    STARTUPINFO si;
    ::ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);

    detail::win32_setup s;
    s.m_work_directory = get_work_directory();
    s.m_startupinfo = &si;

    PROCESS_INFORMATION pi = detail::win32_start(cl, get_environment(),
                                                 behin, behout, beherr,
                                                 get_merge_out_err(), s);

    ph = pi.hProcess;
#endif

    return detail::factories::create_child(ph, fhstdin, fhstdout, fhstderr);
}

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_LAUNCHER_HPP)
