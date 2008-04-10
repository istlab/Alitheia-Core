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
//! \file boost/process/posix_launcher.hpp
//!
//! Includes the declaration of the posix_launcher class.
//!

#if !defined(BOOST_PROCESS_POSIX_LAUNCHER_HPP)
/** \cond */
#define BOOST_PROCESS_POSIX_LAUNCHER_HPP
/** \endcond */

#include <unistd.h>

#include <cerrno>
#include <cstdlib>
#include <set>

#include <boost/process/posix_child.hpp>
#include <boost/process/detail/environment.hpp>
#include <boost/process/detail/posix_ops.hpp>
#include <boost/process/detail/systembuf.hpp>
#include <boost/process/exceptions.hpp>
#include <boost/process/launcher.hpp>
#include <boost/process/stream_behavior.hpp>

namespace boost {
namespace process {

// ------------------------------------------------------------------------

//!
//! \brief POSIX implementation of the Launcher concept.
//!
//! The posix_launcher class implements the Launcher concept with features
//! only available in POSIX systems.  Among these are the ability to set up
//! more than three communication pipes and the possibility to change the
//! security credentials of the spawned process as well as its file system
//! root directory.
//!
//! This class is built on top of the generic launcher so as to allow its
//! trivial adoption.  A program using the generic launcher may grow the
//! need to use some POSIX-specific features.  In that case, adapting the
//! code is only a matter of redefining the appropriate object and later
//! using the required extra features: there should be no need to modify
//! the existing code in any other way.
//!
class posix_launcher :
    public launcher
{
    //!
    //! \brief List of stream merges (source descriptor - target descriptor).
    //!
    detail::merge_set m_merge_set;

    //!
    //! \brief List of input streams that will be redirected.
    //!
    detail::info_map m_input_info;

    //!
    //! \brief List of output streams that will be redirected.
    //!
    detail::info_map m_output_info;

    //!
    //! \brief POSIX-specific properties passed to the new process.
    //!
    detail::posix_setup m_setup;

public:
    //!
    //! \brief Sets up the behavior of an input channel.
    //!
    //! Configures the input descriptor \a desc to behave as described
    //! by \b.  If \a desc matches STDIN_FILENO (defined in cstdlib), this
    //! mimics the set_stdin_behavior() call.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to redirection functions for simplicity.
    //!
    posix_launcher& set_input_behavior(int desc, stream_behavior b);

    //!
    //! \brief Sets up the behavior of an output channel.
    //!
    //! Configures the output descriptor \a desc to behave as described
    //! by \b.  If \a desc matches STDOUT_FILENO or STDERR_FILENO (both
    //! defined in cstdlib), this mimics the set_stdout_behavior() and
    //! set_stderr_behavior() calls respectively.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to redirection functions for simplicity.
    //!
    posix_launcher& set_output_behavior(int desc, stream_behavior b);

    //!
    //! \brief Sets up a merge of two output streams.
    //!
    //! Configures the launcher to merge to output streams; that is, that
    //! when the child process writes to \a from, the data seems to have
    //! been written to \a to.  If \a from matches STDOUT_FILENO and
    //! \a to matches STDERR_FILENO (both defined in cstdlib), this mimics
    //! the REDIR_STDERR_TO_STDOUT flag passed to the constructor.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to redirection functions for simplicity.
    //!
    posix_launcher& merge_outputs(int from, int to);

    //!
    //! \brief Gets the UID that will be used to launch the new child.
    //!
    //! Returns the user identifier that will be used to launch the new
    //! child process.  By default, this matches the user of the parent
    //! process at the moment of the creation of the launcher object.
    //!
    uid_t get_uid(void) const;

    //!
    //! \brief Sets the UID credentials used to launch the new process.
    //!
    //! Sets the UID credentials used to launch the new process to those
    //! given in \a uid.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to configuration function for simplicity.
    //!
    posix_launcher& set_uid(uid_t uid);

    //!
    //! \brief Gets the effective UID that will be used to launch the new
    //!        child.
    //!
    //! Returns the effective user identifier that will be used to launch
    //! the new child process.  By default, this matches the effective user
    //! of the parent process at the moment of the creation of the launcher
    //! object.
    //!
    uid_t get_euid(void) const;

    //!
    //! \brief Sets the effective UID credentials used to launch the new
    //!        process.
    //!
    //! Sets the effective UID credentials used to launch the new process
    //! to those given in \a euid.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to configuration function for simplicity.
    //!
    posix_launcher& set_euid(uid_t euid);

    //!
    //! \brief Gets the GID that will be used to launch the new child.
    //!
    //! Returns the group identifier that will be used to launch the new
    //! child process.  By default, this matches the group of the parent
    //! process at the moment of the creation of the launcher object.
    //!
    gid_t get_gid(void) const;

    //!
    //! \brief Sets the GID credentials used to launch the new process.
    //!
    //! Sets the GID credentials used to launch the new process to those
    //! given in \a gid.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to configuration function for simplicity.
    //!
    posix_launcher& set_gid(gid_t gid);

    //!
    //! \brief Gets the effective GID that will be used to launch the new
    //!        child.
    //!
    //! Returns the effective group identifier that will be used to launch
    //! the new child process.  By default, this matches the effective
    //! group of the parent process at the moment of the creation of the
    //! launcher object.
    //!
    gid_t get_egid(void) const;

    //!
    //! \brief Sets the effective GID credentials used to launch the new
    //!        process.
    //!
    //! Sets the effective GID credentials used to launch the new process
    //! to those given in \a egid.
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to configuration function for simplicity.
    //!
    posix_launcher& set_egid(gid_t egid);

    //!
    //! \brief Gets the directory that the new process will be chrooted to.
    //!
    //! Gets a path to the directory that will be used to chroot the
    //! process to during execution.  If the resulting string is empty,
    //! it means that no chroot shall take place (the default).
    //!
    const std::string& get_chroot(void) const;

    //!
    //! \brief Sets the directory to chroot the new process to.
    //!
    //! Sets the directory that will be used to chroot the process to
    //! during execution.  \a dir may be empty to indicate that the process
    //! should not be chrooted (the default).
    //!
    //! \return A reference to the launcher to allow daisy-chaining calls
    //!         to configuration function for simplicity.
    //!
    posix_launcher& set_chroot(const std::string& dir);

    //!
    //! \brief Starts a new child process.
    //!
    //! Given a command line \a cl, starts a new process with all the
    //! parameters configured in the launcher.  The launcher can be
    //! reused afterwards to launch other different command lines.
    //!
    //! \return A handle to the new child process.
    //!
    template< class Command_Line >
    posix_child start(const Command_Line& cl);
};

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_input_behavior(int desc, stream_behavior b)
{
    if (desc == STDIN_FILENO)
        set_stdin_behavior(b);
    else
        detail::posix_behavior_to_info(b, desc, false, m_input_info);
    return *this;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_output_behavior(int desc, stream_behavior b)
{
    if (desc == STDOUT_FILENO)
        set_stdout_behavior(b);
    else if (desc == STDERR_FILENO)
        set_stderr_behavior(b);
    else
        detail::posix_behavior_to_info(b, desc, true, m_output_info);
    return *this;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::merge_outputs(int src, int dest)
{
    if (src == STDERR_FILENO && dest == STDOUT_FILENO)
        set_merge_out_err(true);
    else
        m_merge_set.insert(std::pair< int, int >(src, dest));
    return *this;
}

// ------------------------------------------------------------------------

inline
uid_t
posix_launcher::get_uid(void)
    const
{
    return m_setup.m_uid;
}

// ------------------------------------------------------------------------

inline
uid_t
posix_launcher::get_euid(void)
    const
{
    return m_setup.m_euid;
}

// ------------------------------------------------------------------------

inline
gid_t
posix_launcher::get_gid(void)
    const
{
    return m_setup.m_gid;
}

// ------------------------------------------------------------------------

inline
gid_t
posix_launcher::get_egid(void)
    const
{
    return m_setup.m_egid;
}

// ------------------------------------------------------------------------

inline
const std::string&
posix_launcher::get_chroot(void)
    const
{
    return m_setup.m_chroot;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_uid(uid_t uid)
{
    m_setup.m_uid = uid;
    return *this;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_euid(uid_t euid)
{
    m_setup.m_euid = euid;
    return *this;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_gid(gid_t gid)
{
    m_setup.m_gid = gid;
    return *this;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_egid(gid_t egid)
{
    m_setup.m_egid = egid;
    return *this;
}

// ------------------------------------------------------------------------

inline
posix_launcher&
posix_launcher::set_chroot(const std::string& dir)
{
    m_setup.m_chroot = dir;
    return *this;
}

// ------------------------------------------------------------------------

template< class Command_Line >
inline
posix_child
posix_launcher::start(const Command_Line& cl)
{
    detail::posix_behavior_to_info(get_stdin_behavior(), STDIN_FILENO,
                                   false, m_input_info);
    detail::posix_behavior_to_info(get_stdout_behavior(), STDOUT_FILENO,
                                   true, m_output_info);
    detail::posix_behavior_to_info(get_stderr_behavior(), STDERR_FILENO,
                                   true, m_output_info);
    if (get_merge_out_err())
        m_merge_set.insert(std::pair< int, int >(STDERR_FILENO,
                                                 STDOUT_FILENO));

    detail::posix_setup s = m_setup;
    s.m_work_directory = get_work_directory();

    pid_t pid = detail::posix_start(cl, posix_launcher::get_environment(),
                                    m_input_info, m_output_info,
                                    m_merge_set, s);

    return detail::factories::create_posix_child(pid, m_input_info,
                                                 m_output_info);
}

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_POSIX_LAUNCHER_HPP)
